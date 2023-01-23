package ua.goit.telegrambot.api.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ua.goit.telegrambot.api.CurrencyJsonUpdate;
import ua.goit.telegrambot.api.dto.Currency;
import ua.goit.telegrambot.api.utils.APIUtilities;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class PrivateBankCurrencyService implements CurrencyService {

    @Override
    public Map<String, Double> getRate(Currency currency) {//repetative code
        //take from other url with GBP
        String fromOther = APIUtilities.writeFromJsonFile(CurrencyJsonUpdate.getADDITIONAL_PATH_PRIVAT());
        log.info(fromOther);

        //take json from file
        String takeJsonFromFile = APIUtilities.writeFromJsonFile(CurrencyJsonUpdate.getABSOLUTE_PATH_PRIVAT());
        log.info(takeJsonFromFile);

        //Convert json => Java Object
        Type typeToken = TypeToken
                .getParameterized(List.class, CurrencyItemPrivat.class)
                .getType();
        List<CurrencyItemPrivat> currencyItemPrivats = new Gson().fromJson(takeJsonFromFile, typeToken);
        currencyItemPrivats.add(getGBP(fromOther, typeToken));


        //Find currency
        double privatBuy = getCurrency(CurrencyItemPrivat::getBuy, currencyItemPrivats, currency);
        double privatSell = getCurrency(CurrencyItemPrivat::getSale, currencyItemPrivats, currency);

        Map<String, Double> rate = new HashMap<>();
        rate.put("buy" + currency, privatBuy);
        rate.put("sell" + currency, privatSell);

        return rate;
    }

    public static double getCurrency(Function<CurrencyItemPrivat, Float> function, List<CurrencyItemPrivat> currencyItemPrivats,
                                     Currency currency) {
        return currencyItemPrivats.stream()
                .filter(it -> it.getCcy() == currency)
                .map(function)
                .collect(Collectors.toList()).get(0);
    }

    private static CurrencyItemPrivat getGBP(String fileWithGBP, Type typeToken){
        List <CurrencyItemPrivat> e = new Gson().fromJson(fileWithGBP, typeToken);
        return e.stream().filter((it) -> it.getCcy() == Currency.GBP).findFirst().orElse(null);
    }

    @Data
    public static class CurrencyItemPrivat {
        private Currency ccy;
        private Currency base_ccy;
        private float buy;
        private float sale;
    }


}
