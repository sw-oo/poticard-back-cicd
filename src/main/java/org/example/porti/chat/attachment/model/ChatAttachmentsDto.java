package org.example.porti.chat.attachment.model;

import lombok.Builder;
import lombok.Getter;

public class ChatAttachmentsDto {
    @Getter
    @Builder
    public static class Res {
        private Long idx;
        private Long chatIdx;
        private String filePath;
        private String fileName;
        private String fileType;
        private Long fileSize;

        public static Res from(ChatAttachments entity) {
            return Res.builder()
                    .idx(entity.getIdx())
                    .chatIdx(entity.getChatMessage().getIdx())
                    .filePath(entity.getFilePath())
                    .fileName(entity.getFileName())
                    .fileType(entity.getFileType())
                    .fileSize(entity.getFileSize())
                    .build();
        }
    }
}
