package com.gym.analytics.mapper;

import com.gym.analytics.dto.MonthlySummary;
import com.gym.analytics.dto.TrainerMonthlySummaryResponse;
import com.gym.analytics.dto.YearlySummary;
import com.gym.analytics.model.Trainer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrainerMapper {

    public TrainerMonthlySummaryResponse toGetTrainerMonthlySummaryResponse(Trainer trainer) {
        return new TrainerMonthlySummaryResponse()
                .firstName(trainer.getFirstName())
                .username(trainer.getUsername())
                .lastName(trainer.getLastName())
                .status(trainer.getStatus() ? TrainerMonthlySummaryResponse.StatusEnum.ACTIVE : TrainerMonthlySummaryResponse.StatusEnum.NOT_ACTIVE)
                .yearlySummaries(mapYearsToDto(trainer.getYears()));
    }

    private List<YearlySummary> mapYearsToDto(List<Trainer.Year> years) {
        if (years == null) {
            return null;
        }

        return years.stream()
                .map(year -> {
                    YearlySummary yearDto = new YearlySummary();
                    yearDto.setYear(year.getYear());
                    yearDto.setMonthlySummaries(mapMonthlySummariesToDto(year.getMonthlySummaries()));
                    return yearDto;
                })
                .collect(Collectors.toList());
    }

    private List<MonthlySummary> mapMonthlySummariesToDto(List<Trainer.Year.MonthlySummary> monthlySummaries) {
        if (monthlySummaries == null) {
            return null;
        }

        return monthlySummaries.stream()
                .map(summary -> {
                    MonthlySummary summaryDto = new MonthlySummary();
                    summaryDto.setMonth(MonthlySummary.MonthEnum.valueOf(summary.getMonth().name()));
                    summaryDto.setTotalDuration(summary.getTrainingSummaryDuration());
                    return summaryDto;
                })
                .collect(Collectors.toList());
    }
}
