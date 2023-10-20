package com.batch.errorhandling.titles;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.kafka.KafkaItemWriter;
import org.springframework.batch.item.kafka.builder.KafkaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TitleBatchConfig {



    private final KafkaTemplate<String,Title> template;

    public TitleBatchConfig(KafkaTemplate<String, Title> template) {
        this.template = template;
    }


    // job configuration with one step
    @Bean
    public Job csvToKafkaJob(JobRepository jobRepository, Step loadCsv){

        return new JobBuilder("csv to kafka_topic-titles-csv",jobRepository)
                .start(loadCsv)
                .incrementer(new RunIdIncrementer())
                .build();

    }



    @Bean // step that load the content of the csv with titlecsvReader
    // wrote them via titleItemWriter
    public Step loadCsv(JobRepository jobRepository, PlatformTransactionManager transactionManager, FlatFileItemReader<Title> titleCsvReader){
        return new StepBuilder("load csv",jobRepository)
                .<Title,Title>chunk(10,transactionManager)
                .reader(titleCsvReader)
                .writer(titleItemWriter())
                .listener(new EmptyInputFailer())
                .faultTolerant()
                .skipLimit(100)
                .skip(Exception.class)
                .listener(new TitleItemListenner())// adding the listenner for logging bad records
                .build();
    }


    @Bean
    // this writer wrote each title to the topic titles.csv
    public KafkaItemWriter<String,Title> titleItemWriter(){
        return new KafkaItemWriterBuilder<String,Title>()
                .kafkaTemplate(this.template)
                .itemKeyMapper(Title::id)
                .build();
    }

    @StepScope
    @Bean
    public FlatFileItemReader<Title> titleCsvReader(@Value("#{jobParameters['titles']}") String inputFile){
        return new FlatFileItemReaderBuilder<Title>()
                .name("titles.csv reader")
                .resource(new FileSystemResource(inputFile))
                .delimited()
                .delimiter(",")
                .names(new String[]{"id",
                        "title",
                        "type",
                        "description",
                        "release_year",
                        "age_certification",
                        "runtime",
                        "genres",
                        "production_countries",
                        "seasons",
                        "imdb_id",
                        "imdb_score",
                        "imdb_votes",
                        "tmdb_popularity",
                        "tmdb_score"
                })
                .linesToSkip(1)
                .fieldSetMapper(fieldSet -> new Title(fieldSet.readString("id"),fieldSet.readString("title"),
                        fieldSet.readString("type"),fieldSet.readString("description"), fieldSet.readInt("release_year"),
                        fieldSet.readString("age_certification"), fieldSet.readInt("runtime"),fieldSet.readString("genres"),fieldSet.readString("production_countries"),
                        fieldSet.readString("seasons"),fieldSet.readString("imdb_id"),fieldSet.readString("imdb_score"),
                        fieldSet.readString("imdb_votes"),fieldSet.readString("tmdb_popularity"),fieldSet.readString("tmdb_score") )
                ).beanMapperStrict(false)
                .build();

    }
}
