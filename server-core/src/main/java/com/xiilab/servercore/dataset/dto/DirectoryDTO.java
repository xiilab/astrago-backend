package com.xiilab.servercore.dataset.dto;

import java.util.List;

import com.xiilab.modulek8sdb.common.enums.FileType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DirectoryDTO {
    private List<ChildrenDTO> children;
    private int directoryCnt;
    private int fileCnt;

    @Getter
    @Builder
    public static class ChildrenDTO {
        private String name;
        private FileType type;
        private String path;
        private String size;
        private String fileCount;
    }
}
