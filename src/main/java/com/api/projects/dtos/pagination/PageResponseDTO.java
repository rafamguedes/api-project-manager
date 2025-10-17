package com.api.projects.dtos.pagination;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO<T> {
  private List<T> content;
  private int currentPage;
  private int totalPages;
  private long totalElements;
  private int size;
  private boolean first;
  private boolean last;

  public static <T> PageResponseDTO<T> of(Page<T> page) {
    return new PageResponseDTO<>(
        page.getContent(),
        page.getNumber(),
        page.getTotalPages(),
        page.getTotalElements(),
        page.getSize(),
        page.isFirst(),
        page.isLast());
  }
}
