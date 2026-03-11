package com.charter.reward_api.dto;

import java.util.List;

/**
 * Data Transfer Object for paginated reward summary responses.
 * Contains customer reward summaries and pagination metadata.
 *
 * @param content list of customer reward summaries for the current page
 * @param page current page number (zero-based)
 * @param size number of items per page
 * @param totalElements total number of customers across all pages
 * @param totalPages total number of pages
 * @param last indicates if this is the last page
 */
public record PagedRewardSummaryDTO(
        List<CustomerRewardSummaryDTO> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
}
