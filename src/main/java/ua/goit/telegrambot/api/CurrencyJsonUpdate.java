package ua.goit.telegrambot.api;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ua.goit.telegrambot.api.dto.BankNAME;
import ua.goit.telegrambot.api.utils.APIUtilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
@Slf4j
public class CurrencyJsonUpdate implements Runnable {
    @Getter
    private static final String ABSOLUTE_PATH_NBU = "src/main/resources/Currency_NBU_rates.json";
    @Getter
    private static final String ABSOLUTE_PATH_PRIVAT = "src/main/resources/Currency_Privat_rates.json";
    @Getter
    private static final String ABSOLUTE_PATH_MONO = "src/main/resources/Currency_Mono_rates.json";
    public static final String NBU_URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
    public static final String PRIVAT_URL = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";
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

        //Mono currency update
        final Runnable mono = () ->
        {
            updateJSON(ABSOLUTE_PATH_MONO, MONO_URL, BankNAME.MONO);
        };

        new Thread(nbu).start();
        log.info("NBU API thread downloader has started");
        new Thread(privat).start();
        log.info("Privat API thread downloader has started");
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
