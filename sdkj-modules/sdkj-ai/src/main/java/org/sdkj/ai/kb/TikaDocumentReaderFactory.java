package org.sdkj.ai.kb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class TikaDocumentReaderFactory {

    public List<Document> read(MultipartFile file) throws IOException {
        Resource resource = new InputStreamResource(file.getInputStream()) {
            @Override public String getFilename() { return file.getOriginalFilename(); }
            @Override public long contentLength() { return file.getSize(); }
        };
        TikaDocumentReader reader = new TikaDocumentReader(resource);
        List<Document> docs = reader.get();
        log.info("Tika parsed {} ({} bytes) into {} document(s)",
            file.getOriginalFilename(), file.getSize(), docs.size());
        return docs;
    }
}
