package com.batch.errorhandling.titles;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

public class EmptyInputFailer implements StepExecutionListener {


    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
     if (stepExecution.getReadCount() > 0){
         return stepExecution.getExitStatus();
     }
     else return ExitStatus.FAILED;
    }
}
