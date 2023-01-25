package ua.goit.telegrambot.api;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ua.goit.telegrambot.api.dto.BankNAME;
import ua.goit.telegrambot.api.utils.APIUtilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class CurrencyJsonUpdate implements Runnable {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
    private static final Date date = new Date();
    @Getter
    private static final String ABSOLUTE_PATH_NBU = "src/main/resources/rates/Currency_NBU_rates.json";
    @Getter
    private static final String ABSOLUTE_PATH_PRIVAT = "src/main/resources/rates/Currency_Privat_rates.json";
    @Getter
    private static final String ADDITIONAL_PATH_PRIVAT = "src/main/resources/rates/Currency_Privat_rates_unusual.json";
    @Getter
    private static final String ABSOLUTE_PATH_MONO = "src/main/resources/rates/Currency_Mono_rates.json";
    public static final String NBU_URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";

    public static final String PRIVAT_URL = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";
    public static final String PRIVAT_URL_NOT_USD_EUR= "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=4";

        public static final String MONO_URL = "https://api.monobank.ua/bank/currency";

    @Override
    public void run() {

        //NBU currency update
        final Runnable nbu = () ->
        {
            updateJSON(ABSOLUTE_PATH_NBU, NBU_URL, BankNAME.NBU);
        };

        //Privat currency update
        final Runnable privat = () ->
        {
            updateJSON(ABSOLUTE_PATH_PRIVAT, PRIVAT_URL, BankNAME.PRIVAT);
        };

        final Runnable privatUnique = () ->
        {
            updateJSON(ADDITIONAL_PATH_PRIVAT, PRIVAT_URL_NOT_USD_EUR, BankNAME.PRIVAT);
        };

        //Mono currency update
        final Runnable mono = () ->
        {
            log.info("Runnable mono started");
            log.info(formatter.format(date));
            updateJSON(ABSOLUTE_PATH_MONO, MONO_URL, BankNAME.MONO);
        };

        new Thread(nbu).start();
        log.info("NBU API thread downloader has started");
        new Thread(privat).start();
        log.info("Privat API thread downloader has started");
        new Thread(privatUnique).start();
        log.info("Privat UNIQUE API thread downloader has started");
        new Thread(mono).start();
        log.info("MonoBank API thread downloader has started");
    }

    private void updateJSON(String filePath, String bankURL, BankNAME bankname) {
        while (true) {

            try {
                threadSleep(3000);
                File file = new File(filePath);
                checkFileExists(file);
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(APIUtilities.getAPIRequest(bankURL));
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }

            } catch (IllegalStateException e) {
                System.err.println(e.getMessage());
                System.err.println("Can't connect to " + bankname + " API");
            } finally {
                threadSleep(360_000);
            }
        }
    }

    private void threadSleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkFileExists(File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();

            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
