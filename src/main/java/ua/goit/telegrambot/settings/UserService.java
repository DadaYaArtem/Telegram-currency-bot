package ua.goit.telegrambot.settings;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Precision;
import ua.goit.telegrambot.api.dto.BankNAME;
import ua.goit.telegrambot.api.dto.Currency;
import ua.goit.telegrambot.api.service.CurrencyService;
import ua.goit.telegrambot.api.service.MonoCurrencyService;
import ua.goit.telegrambot.api.service.NBUCurrencyService;
import ua.goit.telegrambot.api.service.PrivateBankCurrencyService;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
public class UserService {
    private static volatile UserService instance;
    private final StorageOfUsers userStorage;//final

    NBUCurrencyService nbuCurrencyService = new NBUCurrencyService();//why classes do not have static reference methods
    PrivateBankCurrencyService privateBankCurrencyService = new PrivateBankCurrencyService();
    MonoCurrencyService monoCurrencyService = new MonoCurrencyService();

    private UserService() {
        userStorage = StorageOfUsers.getInstance();
    }

    public static UserService getInstance() { //«блокировка с двойной проверкой» (Double-Checked Locking)
        UserService result = instance;
        if (result != null) {
            return result;
        }
        synchronized (UserService.class) {
            if (instance == null) {
                instance = new UserService();
            }
            return instance;
        }
    }

    public void createUser(long userId) {
        userStorage.add(new User(userId));
    }

    public void setBank(long userId, BankNAME bank) {
        userStorage.get(userId).setBank(bank);
    }

    public BankNAME getBank(long userId) {
        return userStorage.get(userId).getBank();
    }

    public void setRounding(long userId, int rounding) {
        userStorage.get(userId).setRounding(rounding);
    }

    public void setUsd(long userId) {
        userStorage.get(userId).setCurrency(Currency.USD);
    }

    public void setEur(long userId) {
        userStorage.get(userId).setCurrency(Currency.EUR);
    }

    public void setGbp(long userId) {
        userStorage.get(userId).setCurrency(Currency.GBP);
    }

    public int getRounding(long userId) {
        return userStorage.get(userId).getRounding();
    }


    public boolean getScheduler(long userId) {
        return userStorage.get(userId).isScheduler();
    }

    public int getSchedulerTime(long userId) {
        return userStorage.get(userId).getSchedulerTime();
    }

    public void setScheduler(long userId, boolean scheduler) {
        userStorage.get(userId).setScheduler(scheduler);
    }

    public void setSchedulerTime(long userId, int time) {
        userStorage.get(userId).setSchedulerTime(time);
    }

    public boolean getEnglish(long userId) {
        return userStorage.get(userId).isEnglish();
    }

    public void setEnglish(long userId, boolean english) {
        userStorage.get(userId).setEnglish(english);
    }

    public void setUkrainian(long userId, boolean english) {
        userStorage.get(userId).setUkrainian(english);
    }

    public Currency getCurrency(long userId) {
        return userStorage.get(userId).getCurrency();
    }

    public List<Long> getUsersWithNotificationOnCurrentHour(int time) {
        return userStorage.getUsersWithNotficationOnCurrentHour(time);
    }


    public String getInfo(long userId) {
        BankNAME bank = getBank(userId);
        Currency currency = getCurrency(userId);
        String result = "";
        String currencyPairUsd = "UAH/USD";
        String currencyPairEur = "UAH/EUR";
        String currencyPairGbp = "UAH/GBP";

        if (bank == BankNAME.NBU) {

            if (currency ==Currency.USD) result = getRateAndFormatCross(userId, "rateUSD", nbuCurrencyService, currencyPairUsd);
            if (currency == Currency.EUR) result = getRateAndFormatCross(userId, "rateEUR", nbuCurrencyService, currencyPairEur);
            if (currency == Currency.GBP) result = getRateAndFormatCross(userId, "rateGBP", nbuCurrencyService, currencyPairGbp);
        }

        if (bank == BankNAME.MONO) {
            if (currency ==Currency.USD) result = getRateAndFormatBuySell(userId,"buyUSD","sellUSD", monoCurrencyService, currencyPairUsd);
            if (currency ==Currency.EUR) result = getRateAndFormatBuySell(userId,"buyEUR","sellEUR", monoCurrencyService, currencyPairEur);
            if (currency ==Currency.GBP) result = getRateAndFormatCross(userId, "crossGBP", monoCurrencyService, currencyPairGbp);

        }

        if (bank == BankNAME.PRIVAT) {
            if (currency ==Currency.USD) result = getRateAndFormatBuySell(userId,"buyUSD","sellUSD", privateBankCurrencyService, currencyPairUsd);
            if (currency ==Currency.EUR) result = getRateAndFormatBuySell(userId,"buyEUR","sellEUR", privateBankCurrencyService, currencyPairEur);
            if (currency ==Currency.GBP) result = getRateAndFormatBuySell(userId,"buyGBP","sellGBP", privateBankCurrencyService, currencyPairGbp);
        }
        log.info(result);
        return result;
    }

    private String getRateAndFormatCross(long userId, String operation, CurrencyService currencyService, String currencyPair) {
        User user = userStorage.get(userId);
        double purchaseRate = currencyService.getRate(user.getCurrency()).get(operation);
        if (user.isEnglish()){
        return MessageFormat
                .format("{0} exchange rate: {1}\n Purchase: {2}\n Sale: ⏳",
                        user.getBank(), currencyPair, String.format("%." + user.getRounding() + "f", purchaseRate));
        }
        else
            return MessageFormat
                    .format("{0} Курс валют: {1}\n Купівля: {2}\n Продаж: ⏳",
                            user.getBank(), currencyPair, String.format("%." + user.getRounding() + "f", purchaseRate));
    }

    private String getRateAndFormatBuySell(long userId, String operation1, String operation2, CurrencyService currencyService, String currencyPair) {
        User user = userStorage.get(userId);
        double buyRate = currencyService.getRate(user.getCurrency()).get(operation1);
        double saleRate = currencyService.getRate(user.getCurrency()).get(operation2);
        if (user.isEnglish()){
        return MessageFormat
                .format("{0} exchange rate: {1}\n Purchase: {2}\n Sale: {3}",
                        user.getBank(), currencyPair, String.format("%." + user.getRounding() + "f", buyRate), String.format("%." + user.getRounding() + "f", saleRate));
        }
        else
            return MessageFormat
                    .format("{0} Курс валют: {1}\n Купівля: {2}\n Продаж: {3}",
                            user.getBank(), currencyPair, String.format("%." + user.getRounding() + "f", buyRate), String.format("%." + user.getRounding() + "f", saleRate));

    }
}
