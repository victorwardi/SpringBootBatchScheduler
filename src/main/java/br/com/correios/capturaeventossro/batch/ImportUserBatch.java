package br.com.correios.capturaeventossro.batch;

import br.com.correios.capturaeventossro.job.processor.PersonItemProcessor;
import br.com.correios.capturaeventossro.job.reader.PersonItemReader;
import br.com.correios.capturaeventossro.job.writer.PersonItemWriter;
import br.com.correios.capturaeventossro.model.Person;
import br.com.correios.capturaeventossro.task.TaskOne;
import br.com.correios.capturaeventossro.task.TaskTwo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;

@Configuration
@EnableBatchProcessing
public class ImportUserBatch extends JobExecutionListenerSupport {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    private static final Logger log = LoggerFactory.getLogger(ImportUserBatch.class);

    @Bean(name = "importUserJob")
    public Job importUserJob() {

        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(this)
                .start(stepOne())
                .next(stepTwo())
                .build();
    }

    @Bean
    public Step stepOne() {
        return stepBuilderFactory.get("stepOne")
                .tasklet(new TaskOne())
                .build();
    }
    @Bean
    public Step stepTwo() {
        return stepBuilderFactory.get("stepTow")
                .tasklet(new TaskTwo())
                .build();
    }

   @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

//            this.jdbcTemplate.query("SELECT first_name, last_name FROM people",
//                    (rs, row) -> new Person(
//                            rs.getString(1),
//                            rs.getString(2))
//            ).forEach(person -> log.info("Found <" + person + "> in the database."));
        }
    }



}
