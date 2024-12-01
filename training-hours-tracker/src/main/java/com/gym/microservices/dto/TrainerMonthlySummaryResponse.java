package com.gym.microservices.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Builder
@Data
public class TrainerMonthlySummaryResponse implements Serializable {

    private String username;
    private String firstName;
    private String lastName;
    private Status Status;

    private List<YearlySummary> yearlySummaries;

    @Data
    @Getter
    public static class YearlySummary implements Serializable {
        private int year;
        private List<MonthlySummary> monthlySummaries = new ArrayList<>();
    }

    @AllArgsConstructor
    @Data
    public static class MonthlySummary implements Serializable {
        private Month month;
        private Double totalDuration;
    }

    public enum Status {
        ACTIVE, NOT_ACTIVE
    }

    public void addDurationToYearlySummary(LocalDate trainingDate, Double duration) {
        int year = trainingDate.getYear();
        Month month = trainingDate.getMonth();

        YearlySummary yearlySummary = getYearlySummary(year);

        updateMonthlySummary(duration, month, yearlySummary);
    }

    private void updateMonthlySummary(Double duration, Month month, YearlySummary yearlySummary) {
        Optional<MonthlySummary> existingMonthlySummary = yearlySummary.getMonthlySummaries().stream()
                .filter(e -> e.getMonth() == month)
                .findFirst();

        if (existingMonthlySummary.isPresent()) {
            MonthlySummary monthlySummary = existingMonthlySummary.get();
            monthlySummary.setTotalDuration(monthlySummary.getTotalDuration() + duration);
        } else {
            MonthlySummary monthlySummary = new MonthlySummary(month, duration);
            yearlySummary.getMonthlySummaries().add(monthlySummary);
        }
    }

    private YearlySummary getYearlySummary(int year) {
        if (yearlySummaries == null) {
            yearlySummaries = new ArrayList<>();
        }

        return yearlySummaries.stream()
                .filter(e -> e.getYear() == year)
                .findFirst()
                .orElseGet(() -> {
                    YearlySummary newYearlySummary = new YearlySummary();
                    newYearlySummary.setYear(year);
                    yearlySummaries.add(newYearlySummary);
                    return newYearlySummary;
                });
    }
}
