package com.gym.analytics.service.impl;

import com.gym.analytics.dto.MonthlySummary;
import com.gym.analytics.dto.TrainerMonthlySummaryResponse;
import com.gym.analytics.dto.YearlySummary;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrainingSummaryManager {

    public void addDurationToYearlySummary(TrainerMonthlySummaryResponse response, LocalDate trainingDate, Double duration) {
        int year = trainingDate.getYear();
        Month month = trainingDate.getMonth();

        YearlySummary yearlySummary = getYearlySummary(response, year);

        updateMonthlySummary(duration, month, yearlySummary);

    }

    private void updateMonthlySummary(Double duration, Month month, YearlySummary yearlySummary) {
        Optional<MonthlySummary> existingMonthlySummary = yearlySummary.getMonthlySummaries().stream()
                .filter(e -> Month.valueOf(e.getMonth().toString()) == month)
                .findFirst();

        if (existingMonthlySummary.isPresent()) {
            MonthlySummary monthlySummary = existingMonthlySummary.get();
            monthlySummary.setTotalDuration(monthlySummary.getTotalDuration() + duration);
        } else {
            MonthlySummary monthlySummary = new MonthlySummary();
            monthlySummary.setMonth(MonthlySummary.MonthEnum.valueOf(month.name()));
            monthlySummary.setTotalDuration(duration);
            yearlySummary.getMonthlySummaries().add(monthlySummary);
        }
    }

    private YearlySummary getYearlySummary(TrainerMonthlySummaryResponse response, int year) {
        if (response.getYearlySummaries() == null) {
            response.setYearlySummaries(new ArrayList<>());
        }

        return response.getYearlySummaries().stream()
                .filter(e -> e.getYear() == year)
                .findFirst()
                .orElseGet(() -> {
                    YearlySummary newYearlySummary = new YearlySummary();
                    newYearlySummary.setYear(year);
                    response.getYearlySummaries().add(newYearlySummary);
                    return newYearlySummary;
                });
    }
}
