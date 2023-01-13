package ua.goit.telegrambot.api.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import ua.goit.telegrambot.api.CurrencyJsonUpdate;
import ua.goit.telegrambot.api.dto.Currency;
import ua.goit.telegrambot.api.utils.Utilities;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Slf4j
public class MonoCurrencyService implements CurrencyService {

    @Override
    public Map<String, Double> getRate(Currency currency) {

        //take json from file
        String takeJsonFromFile = Utilities.writeFromJsonFile(CurrencyJsonUpdate.getABSOLUTE_PATH_MONO());
        log.info(takeJsonFromFile);

        log.info(takeJsonFromFile);


        //replace for enum Currency
        String replaceJson = takeJsonFromFile
                .replace(":840", ":USD")
                .replace(":978", ":EUR")
                .replace(":980", ":UAH")
                .replace(":826", ":GBP");

        //Convert json => Java Object
        Type typeToken = TypeToken
                .getParameterized(List.class, CurrencyItemMono.class)
                .getType();
        List<CurrencyItemMono> currencyItemMono = new Gson().fromJson(replaceJson, typeToken);

        if (currency == Currency.GBP) {
            double monoCrossCurseGBP = currencyItemMono.stream()
                    .filter(it -> it.getCurrencyCodeA() == currency)
                    .map(CurrencyItemMono::getRateCross)
                    .collect(Collectors.toList()).get(0);

            Map<String, Double> rate = new HashMap<>();
            rate.put("cross" + currency, monoCrossCurseGBP);
            return rate;
        } else {
            double monoBuy = currencyItemMono.stream()
                    .filter(it -> it.getCurrencyCodeA() == currency)
                    .map(CurrencyItemMono::getRateBuy)
                    .collect(Collectors.toList()).get(0);

            double monoSell = currencyItemMono.stream()
                    .filter(it -> it.getCurrencyCodeA() == Currency.EUR)
                    .map(CurrencyItemMono::getRateSell)
                    .collect(Collectors.toList()).get(0);

            Map<String, Double> rate = new HashMap<>();
            rate.put("buy" + currency, monoBuy);
            rate.put("Sell" + currency, monoSell);

            return rate;
        }
    }

    @Data
    public static class CurrencyItemMono {
        private Currency currencyCodeA;
        private Currency currencyCodeB;
        private int date;
        private float rateBuy;
        private float rateSell;
        private float rateCross;
    }

}
