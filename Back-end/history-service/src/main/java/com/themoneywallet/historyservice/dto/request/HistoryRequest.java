package com.themoneywallet.historyservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.themoneywallet.historyservice.entity.fixed.TimeInterval;
import com.themoneywallet.historyservice.event.EventType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public class HistoryRequest {

    @JsonProperty("user_id")
    @NotBlank(message = "User ID cannot be blank")
    @Size(max = 100, message = "User ID cannot exceed 100 characters")
    private String userId;

    @JsonProperty("start_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @JsonProperty("end_date")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    @JsonProperty("event_types")
    private List<EventType> eventTypes;

    @JsonProperty("page")
    @Min(value = 0, message = "Page number cannot be negative")
    @Builder.Default
    private Integer page = 0;

    @JsonProperty("size")
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 1000, message = "Page size cannot exceed 1000")
    @Builder.Default
    private Integer size = 20;

    @JsonProperty("sort_by")
    @Builder.Default
    private String sortBy = "timestamp";

    @JsonProperty("sort_direction")
    @Pattern(
        regexp = "^(ASC|DESC)$",
        message = "Sort direction must be ASC or DESC"
    )
    @Builder.Default
    private String sortDirection = "DESC";

    @JsonProperty("search_text")
    @Size(max = 500, message = "Search text cannot exceed 500 characters")
    private String searchText;

    @JsonProperty("categories")
    private List<String> categories;

    @JsonProperty("time_interval")
    private TimeInterval timeInterval;

    @JsonProperty("include_event_data")
    @Builder.Default
    private Boolean includeEventData = true;
}
