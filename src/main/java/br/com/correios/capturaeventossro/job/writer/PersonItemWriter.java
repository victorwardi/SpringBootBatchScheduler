package br.com.correios.capturaeventossro.job.writer;

import br.com.correios.capturaeventossro.model.Person;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
@Component
public class PersonItemWriter extends JdbcBatchItemWriter<Person> {

    DataSource dataSource;

    public PersonItemWriter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    @Transactional
    public void write(List<? extends Person> users) throws Exception {

        new JdbcBatchItemWriterBuilder<Person>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
                .dataSource(this.dataSource)
                .build();

    }
}
