package com.xiilab.servercore.dataset.dto;

import java.util.List;

import com.xiilab.servercore.common.enums.FileType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DirectoryDTO {
    private List<ChildrenDTO> children;


    @Getter
    @Builder
    public static class ChildrenDTO {
        private String name;
        private FileType type;
        private String path;
    }
}