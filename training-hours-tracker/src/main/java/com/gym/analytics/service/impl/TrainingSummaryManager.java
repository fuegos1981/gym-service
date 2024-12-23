package com.gym.analytics.service.impl;

import com.gym.analytics.model.Trainer;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TrainingSummaryManager {

    public void addDurationToYearlySummary(Trainer trainer, LocalDate trainingDate, Double duration) {
        int year = trainingDate.getYear();
        Month month = trainingDate.getMonth();

        Trainer.Year yearlySummary = getYearlySummary(trainer, year);

        updateMonthlySummary(duration, month, yearlySummary);

        if (yearlySummary.getMonthlySummaries().isEmpty()) {
            trainer.getYears().remove(yearlySummary);
        }
    }

    private void updateMonthlySummary(Double duration, Month month, Trainer.Year yearlySummary) {

        List<Trainer.Year.MonthlySummary> monthlySummaries = yearlySummary.getMonthlySummaries();
        Optional<Trainer.Year.MonthlySummary> existingMonthlySummary = monthlySummaries.stream()
                .filter(e -> e.getMonth() == month)
                .findFirst();

        if (existingMonthlySummary.isPresent()) {
            setNewDuration(duration, monthlySummaries, existingMonthlySummary.get());
            return;
        }

        Trainer.Year.MonthlySummary monthlySummary = new Trainer.Year.MonthlySummary();
        monthlySummary.setMonth((month));
        monthlySummary.setTrainingSummaryDuration(duration);
        yearlySummary.getMonthlySummaries().add(monthlySummary);
    }

    private void setNewDuration(Double duration, List<Trainer.Year.MonthlySummary> monthlySummaries, Trainer.Year.MonthlySummary existingMonthlySummary) {
        double newDuration = existingMonthlySummary.getTrainingSummaryDuration() + duration;

        if (newDuration == 0.0) {
            monthlySummaries.remove(existingMonthlySummary);
        } else {
            existingMonthlySummary.setTrainingSummaryDuration(newDuration);
        }
    }

    private Trainer.Year getYearlySummary(Trainer trainer, int year) {
        if (trainer.getYears() == null) {
            trainer.setYears(new ArrayList<>());
        }

        return trainer.getYears().stream()
                .filter(e -> e.getYear() == year)
                .findFirst()
                .orElseGet(() -> buildNewYearSummary(trainer, year));
    }

    private Trainer.Year buildNewYearSummary(Trainer trainer, int year) {
        Trainer.Year newYear = new Trainer.Year();
        newYear.setYear(year);
        trainer.getYears().add(newYear);
        return newYear;
    }
}
