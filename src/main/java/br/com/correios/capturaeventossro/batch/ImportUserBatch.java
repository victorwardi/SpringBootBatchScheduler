package br.com.correios.capturaeventossro.batch;

import br.com.correios.capturaeventossro.job.processor.PersonItemProcessor;
import br.com.correios.capturaeventossro.job.reader.PersonItemReader;
import br.com.correios.capturaeventossro.job.writer.PersonItemWriter;
import br.com.correios.capturaeventossro.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@EnableBatchProcessing
public class ImportUserBatch extends JobExecutionListenerSupport {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    PersonItemProcessor processor;

    @Autowired
    PersonItemWriter writer;

    @Autowired
    PersonItemReader reader;

    private static final Logger log = LoggerFactory.getLogger(ImportUserBatch.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ImportUserBatch(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Bean(name = "importUserJob")
    public Job importUserJob(Step step) {

        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(this)
                .flow(step(writer))
                .end()
                .build();
    }

    @Bean
    public Step step(JdbcBatchItemWriter<Person> writer) {
        return stepBuilderFactory.get("step")
                .<Person, Person> chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }



    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");

            this.jdbcTemplate.query("SELECT first_name, last_name FROM people",
                    (rs, row) -> new Person(
                            rs.getString(1),
                            rs.getString(2))
            ).forEach(person -> log.info("Found <" + person + "> in the database."));
        }
    }



}
