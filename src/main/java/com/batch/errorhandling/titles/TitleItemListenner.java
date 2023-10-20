package com.batch.errorhandling.titles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.batch.item.file.FlatFileParseException;

public class TitleItemListenner implements ItemReadListener {

    private final static Logger logger = LoggerFactory.getLogger(TitleItemListenner.class);

    @Override
    public void onReadError(Exception ex) {
        if (ex instanceof FlatFileParseException parseException){

            String builder = "an error has occurred when reading "
                    + parseException.getLineNumber()
                    + " the line. Here are its details about the bad input\n "
                    + parseException.getInput()
                    + "\n";

            logger.error(builder,parseException);
        }else {
            logger.error("An error occur ", ex);
        }
    }
}
