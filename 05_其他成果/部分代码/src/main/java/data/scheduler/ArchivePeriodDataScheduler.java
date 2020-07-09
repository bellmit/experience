package data.scheduler;

import data.service.EntityDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import period.dto.PeriodType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;


@Configuration
@EnableScheduling
public class ArchivePeriodDataScheduler {
    private EntityDataService entityDataService;

    @Autowired
    public ArchivePeriodDataScheduler(EntityDataService entityDataService) {
        this.entityDataService = entityDataService;
    }

    @Scheduled(cron = "${data.archive.daily:0 0 2 * * ?}")
    public void daily() {
        LocalDateTime time = LocalDate.now().minusDays(1).atStartOfDay();

        entityDataService.archive(PeriodType.DAY, time);
    }

    @Scheduled(cron = "${data.archive.monthly:0 0 0 2 * ?}")
    public void monthly() {
        LocalDateTime time = YearMonth.now().minusMonths(1).atDay(1).atStartOfDay();

        entityDataService.archive(PeriodType.MONTH, time);
    }

    @Scheduled(cron = "${data.archive.monthly:0 0 20 1 1 ?}")
    public void yearly() {
        LocalDateTime time = Year.now().minusYears(1).atDay(1).atStartOfDay();

        entityDataService.archive(PeriodType.YEAR, time);
    }
}
