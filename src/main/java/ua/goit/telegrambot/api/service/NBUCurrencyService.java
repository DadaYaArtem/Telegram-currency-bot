package ua.goit.telegrambot.api.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ua.goit.telegrambot.api.CurrencyJsonUpdate;
import ua.goit.telegrambot.api.dto.Currency;
import ua.goit.telegrambot.api.utils.APIUtilities;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
@Slf4j
public class NBUCurrencyService implements CurrencyService {

    @Override
    public Map<String, Double> getRate(Currency currency) {

        //take json from file
        String takeJsonFromFile = APIUtilities.writeFromJsonFile(CurrencyJsonUpdate.getABSOLUTE_PATH_NBU());
        log.info(takeJsonFromFile);

        //Convert json => Java Object
        Type typeToken = TypeToken
                .getParameterized(List.class, CurrencyItemNBU.class)
                .getType();
        List<CurrencyItemNBU> currencyItemsNBU = new Gson().fromJson(takeJsonFromFile, typeToken);

        //Find currency
        double currencyRate = getCurrency( CurrencyItemNBU::getRate, currencyItemsNBU, currency);

        Map<String, Double> rate = new HashMap<>();
        rate.put("rate" + currency, currencyRate);

        return rate;
    }

    private static Float getCurrency(Function<CurrencyItemNBU, Float> function, List<CurrencyItemNBU> currencyItemsNBU, Currency currency) {
        return currencyItemsNBU.stream()
                .filter(it -> it.getCc() == currency)
                .map(function)
                .collect(Collectors.toList()).get(0);
    }

    @Data
    public static class CurrencyItemNBU {
        private int r030;
        private String txt;
        private Float rate;
        private Currency cc;
        private String exchangeDate;
    }

}
