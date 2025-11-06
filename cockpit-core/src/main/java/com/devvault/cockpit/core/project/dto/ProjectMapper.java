package com.devvault.cockpit.core.project.dto;

import com.devvault.cockpit.core.project.Project;

public class ProjectMapper {

    public static Project toEntity(ProjectDto dto) {
        if (dto == null) return null;
        Project p = new Project();
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setTags(dto.getTags());
        p.setImportStatus(null);
        p.setImportMessage(null);
        return p;
    }

    public static ProjectDto toDto(Project p) {
        if (p == null) return null;
        ProjectDto dto = new ProjectDto();
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setTags(p.getTags());
        return dto;
    }

    public static com.devvault.cockpit.core.project.dto.ProjectResponseDto toResponseDto(Project p) {
        if (p == null) return null;
        com.devvault.cockpit.core.project.dto.ProjectResponseDto r = new com.devvault.cockpit.core.project.dto.ProjectResponseDto();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setDescription(p.getDescription());
        r.setTags(p.getTags());
        r.setCreatedAt(p.getCreatedAt());
        r.setImportStatus(p.getImportStatus());
        r.setImportMessage(p.getImportMessage());
        r.setImportStartedAt(p.getImportStartedAt());
        r.setImportFinishedAt(p.getImportFinishedAt());
        return r;
    }
}
