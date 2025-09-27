package com.kk.etl;


import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.jsoup.JsoupDocumentReader;
import org.springframework.ai.reader.jsoup.config.JsoupDocumentReaderConfig;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DocumentETLPipeline {

    @jakarta.annotation.Resource
    private OpenAiChatModel chatModel;

    /**
     * PDF文档处理完整流程
     */
    public List<Document> processPdfDocument(Resource pdfResource) {
        // 1. 读取PDF文档
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(pdfResource,
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(
                                ExtractedTextFormatter.builder()
                                        .withNumberOfTopTextLinesToDelete(0)
                                        .build())
                        .withPagesPerDocument(1)
                        .build());

        // 2. 文档拆分
        TokenTextSplitter splitter = new TokenTextSplitter(
                1000,  // 默认块大小（tokens）
                400,   // 最小块大小（字符）
                10,    // 最小嵌入长度
                5000,  // 最大块数量
                true   // 保留分隔符
        );

        // 3. 关键词提取增强
        // KeywordMetadataEnricher keywordEnricher = new KeywordMetadataEnricher(chatModel, 5);

        // 4. 摘要增强
        /*SummaryMetadataEnricher summaryEnricher = new SummaryMetadataEnricher(
                chatModel,
                List.of(SummaryMetadataEnricher.SummaryType.CURRENT, SummaryMetadataEnricher.SummaryType.PREVIOUS, SummaryMetadataEnricher.SummaryType.NEXT)
        );*/

        // 5. 执行ETL流水线
        List<Document> documents = pdfReader.read();
        // List<Document> enrichedDocuments = keywordEnricher.apply(splitDocuments);

        // 6. 返回文档
        // return summaryEnricher.apply(enrichedDocuments);
        return splitter.apply(documents);

    }

    /**
     * 文本文档处理
     */
    public List<Document> processTextDocument(Resource textResource) {
        // 1. 读取文本
        TextReader textReader = new TextReader(textResource);
        textReader.getCustomMetadata().put("source", "sample.txt");
        textReader.getCustomMetadata().put("type", "text");

        // 2. 拆分和增强
        TokenTextSplitter splitter = new TokenTextSplitter();

        // 3.返回
        return splitter.apply(textReader.read());
    }

    /**
     * Markdown文档处理
     */
    public List<Document> processMarkdownDocument(Resource mdResource) {
        // 1. 读取Markdown
        MarkdownDocumentReader mdReader = new MarkdownDocumentReader(mdResource,
                MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(true)
                        .withAdditionalMetadata("format", "markdown")
                        .build());

        // 2. 拆分
        TokenTextSplitter splitter = new TokenTextSplitter(800, 350, 5, 10000, true);

        // 3.返回
        return splitter.apply(mdReader.read());
    }

    /**
     * HTML文档处理
     */
    public List<Document> processHtmlDocument(Resource htmlResource) {
        // 1. 读取HTML
        JsoupDocumentReaderConfig config = JsoupDocumentReaderConfig.builder()
                .selector("article p, div.content p") // 提取特定段落
                .charset("UTF-8")
                .includeLinkUrls(true)
                .metadataTags(List.of("description", "keywords", "author"))
                .additionalMetadata("source", "web-page")
                .build();

        JsoupDocumentReader htmlReader = new JsoupDocumentReader(htmlResource, config);

        // 2. 拆分
        TokenTextSplitter splitter = new TokenTextSplitter();

        // 3.返回
        return splitter.apply(htmlReader.read());
    }

    /**
     * JSON文档处理
     */
    public List<Document> processJsonDocument(Resource jsonResource) {
        // 1. 读取JSON
        JsonReader jsonReader = new JsonReader(jsonResource, "description", "content", "summary");

        // 2. 使用JSON指针提取特定数据
        List<Document> documents = jsonReader.get("/products"); // 提取products数组

        // 3. 拆分
        TokenTextSplitter splitter = new TokenTextSplitter();

        // 4.返回
        return splitter.apply(documents);
    }

    /**
     * Office文档处理（使用Tika）
     */
    public List<Document> processOfficeDocument(Resource docResource) {
        // 1. 使用Tika读取
        TikaDocumentReader tikaReader = new TikaDocumentReader(docResource);

        // 2. 拆分
        TokenTextSplitter splitter = new TokenTextSplitter();

        // 3. 增强元数据
        KeywordMetadataEnricher keywordEnricher = new KeywordMetadataEnricher(chatModel, 3);

        // 4.执行流水线
        List<Document> documents = tikaReader.read();
        List<Document> splitDocs = splitter.apply(documents);

        // 5.返回
        return keywordEnricher.apply(splitDocs);
    }

    /**
     * 批量处理多种文档类型
     */
    public List<Document> processBatchDocuments(@NotNull List<Resource> resources) {
        List<Document> d = new ArrayList<>();
        for (Resource resource : resources) {
            try {
                // 1. 获取文件名
                String filename = resource.getFilename();
                // 2.根据文件类型选择合适的reader
                assert filename != null;
                List<Document> documents;
                if (filename.endsWith(".pdf")) {
                    documents = processPdfDocument(resource);
                } else if (filename.endsWith(".txt")) {
                    documents = processTextDocument(resource);
                } else if (filename.endsWith(".md")) {
                    documents = processMarkdownDocument(resource);
                } else if (filename.endsWith(".html")) {
                    documents = processHtmlDocument(resource);
                } else {
                    documents = processOfficeDocument(resource);
                }
                d.addAll(documents);

            } catch (Exception e) {
                log.error("处理文件失败: {},错误信息: {}", resource.getFilename(), e.getMessage());
            }
        }
        return d;
    }
}
