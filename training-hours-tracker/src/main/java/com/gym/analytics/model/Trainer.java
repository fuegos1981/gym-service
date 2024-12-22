package com.gym.analytics.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@Document(collection = "trainers")
public class Trainer {
    @Id
    private String id;

    @Field("username")
    private String username;

    @Field("firstName")
    private String firstName;

    @Field("lastName")
    private String lastName;

    @Field("status")
    private Boolean status;

    @Field("years")
    private List<Year> years;


    @Data
    public static class Year {
        @Field("year")
        private int year;

        @Field("months")
        private List<MonthlySummary> monthlySummaries = new ArrayList<>();

        @Data
        public static class MonthlySummary {
            @Field("month")
            private Month month;

            @Field("trainingSummaryDuration")
            private Double trainingSummaryDuration;
        }
    }
}
