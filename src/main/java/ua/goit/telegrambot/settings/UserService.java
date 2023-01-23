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
    private StorageOfUsers userStorage;//final

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

    public void setUsd(long userId, boolean usd) {
        userStorage.get(userId).setUsd(usd);
    }

    public void setEur(long userId, boolean eur) {
        userStorage.get(userId).setEur(eur);
    }

    public void setGbp(long userId, boolean gbp) {
        userStorage.get(userId).setGbp(gbp);
    }

    public int getRounding(long userId) {
        return userStorage.get(userId).getRounding();
    }

    public boolean getUsd(long userId) {
        return userStorage.get(userId).isUsd();
    }

    public boolean getEur(long userId) {
        return userStorage.get(userId).isEur();
    }

    public boolean getGbp(long userId) {
        return userStorage.get(userId).isGbp();
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

    public boolean getUkrainian(long userId) {
        return userStorage.get(userId).isUkrainian();
    }

    public void setEnglish(long userId, boolean english) {
        userStorage.get(userId).setEnglish(english);
    }

    public void setUkrainian(long userId, boolean english) {
        userStorage.get(userId).setUkrainian(english);
    }


    public String getCurrency(long userId) {
        if (getUsd(userId)) {
            return "usd";
        } else if (getEur(userId)) {
            return "eur";
        } else {
            return "gbp";
        }
    }

    public List<Long> getUsersWithNotificationOnCurrentHour(int time) {
        return userStorage.getUsersWithNotficationOnCurrentHour(time);
    }


    public String getInfo(long userId) {//lots of repetative code
        BankNAME bank = getBank(userId);
        int rounding = getRounding(userId);
        String result = "";
        String currencyPairUsd = "UAH/USD";
        String currencyPairEur = "UAH/EUR";
        String currencyPairGbp = "UAH/GBP";
        if (bank == BankNAME.NBU) {

            if (getUsd(userId)) result = checkForCurrencyAndPringOneRate(Currency.USD, BankNAME.NBU, "rateUSD", nbuCurrencyService, rounding, currencyPairUsd);
            if (getEur(userId)) result = checkForCurrencyAndPringOneRate(Currency.EUR, BankNAME.NBU, "rateEUR", nbuCurrencyService, rounding, currencyPairEur);
            if (getGbp(userId)) result = checkForCurrencyAndPringOneRate(Currency.GBP, BankNAME.NBU, "rateGBP", nbuCurrencyService, rounding, currencyPairGbp);
        }

        if (bank == BankNAME.MONO) {
            if (getUsd(userId)) result = checkForCurrencyAndPringBuySale(Currency.USD, BankNAME.MONO, "buyUSD","sellUSD", monoCurrencyService, rounding, currencyPairUsd);
            if (getEur(userId)) result = checkForCurrencyAndPringBuySale(Currency.EUR, BankNAME.MONO, "buyEUR","sellEUR", monoCurrencyService, rounding, currencyPairUsd);
            if (getGbp(userId)) result = checkForCurrencyAndPringOneRate(Currency.GBP, BankNAME.MONO, "crossGBP", monoCurrencyService, rounding, currencyPairUsd);

        }

        if (bank == BankNAME.PRIVAT) {
            if (getUsd(userId)) result = checkForCurrencyAndPringBuySale(Currency.USD, BankNAME.PRIVAT, "buyUSD","sellUSD", privateBankCurrencyService, rounding, currencyPairUsd);
            if (getEur(userId)) result = checkForCurrencyAndPringBuySale(Currency.EUR, BankNAME.PRIVAT, "buyEUR","sellEUR", privateBankCurrencyService, rounding, currencyPairUsd);
            if (getGbp(userId)) result = checkForCurrencyAndPringBuySale(Currency.GBP, BankNAME.PRIVAT, "buyGBP","sellGBP", privateBankCurrencyService, rounding, currencyPairGbp);
        }
        log.info(result);
        return result;
    }

    private static String checkForCurrencyAndPringOneRate(Currency currency, BankNAME bankNAME, String operation, CurrencyService currencyService, int rounding, String currencyPair) {
        double purchaseRate = currencyService.getRate(currency).get(operation);
        return MessageFormat
                .format("{0} exchange rate: {1}\n Purchase: {2}\n Sale: ⏳",
                        bankNAME, currencyPair, String.format("%." + rounding + "f", purchaseRate));
    }

    private static String checkForCurrencyAndPringBuySale(Currency currency, BankNAME bankNAME, String operation1, String operation2, CurrencyService currencyService, int rounding, String currencyPair) {
        log.info("currency:" + currency);
        log.info("bankNAME:" + bankNAME);
        log.info("operation1:" + operation1);
        log.info("operation2:" + operation2);
        log.info("rounding:" + rounding);
        log.info("currencyService:" + currencyService);
        log.info("currencyPair:" + currencyPair);
        log.info("mono getRate: " + currencyService.getRate(currency).toString());
        double buyRate = currencyService.getRate(currency).get(operation1);
        log.info(String.valueOf(buyRate));
        double saleRate = currencyService.getRate(currency).get(operation2);
        log.info(String.valueOf(saleRate));
        return MessageFormat
                .format("{0} exchange rate: {1}\n Purchase: {2}\n Sale: {3}",
                        bankNAME, currencyPair, String.format("%." + rounding + "f", buyRate), String.format("%." + rounding + "f", saleRate));
    }

    public String getInfoUkr(long userId) {
        BankNAME bank = getBank(userId);
        int rounding = getRounding(userId);
        String result = "";
        String currencyPairUsd = "UAH/USD";
        String currencyPairEur = "UAH/EUR";
        String currencyPairGbp = "UAH/GBP";
        if (bank == BankNAME.NBU) {

            if (getUsd(userId)) {
                double purchaseRate1 = nbuCurrencyService.getRate(Currency.USD).get("rateUSD");
                double purchaseRate = Precision.round(purchaseRate1, rounding);
                result = MessageFormat
                        .format("{0} курс валют: {1}\n купівля: {2}\n продаж: ⏳ ", "НБУ", currencyPairUsd, String.format("%." + rounding + "f", purchaseRate));

            }
            if (getEur(userId)) {
                double purchaseRate1 = nbuCurrencyService.getRate(Currency.EUR).get("rateEUR");
                double purchaseRate = Precision.round(purchaseRate1, rounding);
                result = MessageFormat
                        .format("{0} курс валют: {1}\n купівля: {2}\n продаж: ⏳ ", "НБУ", currencyPairEur, String.format("%." + rounding + "f", purchaseRate));

            }
            if (getGbp(userId)) {
                double purchaseRate1 = nbuCurrencyService.getRate(Currency.GBP).get("rateGBP");
                double purchaseRate = Precision.round(purchaseRate1, rounding);
                result = MessageFormat
                        .format("{0} курс валют: {1}\n купівля: {2}\n продаж: ⏳ ", "НБУ", currencyPairGbp, String.format("%." + rounding + "f", purchaseRate));

            }
        }

        if (bank == BankNAME.MONO) {
            if (getUsd(userId)) {
                double purchaseRate = monoCurrencyService.getRate(Currency.USD).get("buyUSD");
                double saleRate = monoCurrencyService.getRate(Currency.USD).get("sellUSD");
                if (saleRate == 0) {
                    result = MessageFormat
                            .format("{0} курс валют: {1}\n купівля: {2}\n продаж: ⏳ ", "МоноБанк", currencyPairUsd, String.format("%." + rounding + "f", purchaseRate));
                } else {
                    result = MessageFormat
                            .format("{0} курс валют: {1}\n купівля: {2}\n продаж: {3}", "МоноБанк", currencyPairUsd, String.format("%." + rounding + "f", purchaseRate), String.format("%." + rounding + "f", saleRate));
                }
            }
            if (getEur(userId)) {
                double purchaseRate = monoCurrencyService.getRate(Currency.EUR).get("buyEUR");
                double saleRate = monoCurrencyService.getRate(Currency.EUR).get("sellEUR");

                if (saleRate == 0) {
                    result = MessageFormat
                            .format("{0} курс валют: {1}\n купівля: {2}\n продаж: ⏳ ", "МоноБанк", currencyPairEur, String.format("%." + rounding + "f", purchaseRate));
                } else {
                    result = MessageFormat
                            .format("{0} курс валют: {1}\n купівля: {2}\n продаж: {3}", "МоноБанк", currencyPairEur, String.format("%." + rounding + "f", purchaseRate), String.format("%." + rounding + "f", saleRate));
                }
            }
            if (getGbp(userId)) {
                double purchaseRate1 = monoCurrencyService.getRate(Currency.GBP).get("crossGBP");
                double purchaseRate = Precision.round(purchaseRate1, rounding);
                result = MessageFormat
                        .format("{0} курс валют: {1}\n купівля: {2}\n продаж: ⏳ ", "МоноБанк", currencyPairGbp, String.format("%." + rounding + "f", purchaseRate));

            }
        }

        if (bank == BankNAME.PRIVAT) {
            if (getUsd(userId)) {
                double purchaseRate = privateBankCurrencyService.getRate(Currency.USD).get("buyUSD");
                double saleRate = privateBankCurrencyService.getRate(Currency.USD).get("sellUSD");
                if (saleRate == 0) {
                    result = MessageFormat
                            .format("{0} курс валют: {1}\n купівля: {2}\n продаж: ⏳ ", "ПриватБанк", currencyPairUsd, String.format("%." + rounding + "f", purchaseRate));
                } else {
                    result = MessageFormat
                            .format("{0} курс валют: {1}\n купівля: {2}\n продаж: {3}", "ПриватБанк", currencyPairUsd, String.format("%." + rounding + "f", purchaseRate), String.format("%." + rounding + "f", saleRate));
                }
            }
            if (getEur(userId)) {
                double purchaseRate = privateBankCurrencyService.getRate(Currency.EUR).get("sellEUR");
                double saleRate = privateBankCurrencyService.getRate(Currency.EUR).get("buyEUR");
                if (saleRate == 0) {
                    result = MessageFormat
                            .format("{0} курс валют: {1}\n купівля: {2}\n продаж: ⏳ ", "ПриватБанк", currencyPairEur, String.format("%." + rounding + "f", saleRate));
                } else {
                    result = MessageFormat
                            .format("{0} курс валют: {1}\n купівля: {2}\n продаж: {3}", "ПриватБанк", currencyPairEur, String.format("%." + rounding + "f", saleRate), String.format("%." + rounding + "f", purchaseRate));
                }
            }
            if (getGbp(userId)) {
                double purchaseRate1 = nbuCurrencyService.getRate(Currency.GBP).get("rateGBP");
                double purchaseRate = Precision.round(purchaseRate1, rounding);
                result = MessageFormat
                        .format("{0} курс валют: {1}\n купівля: {2}\n продаж: ⏳ ", "ПриватБанк", currencyPairGbp, String.format("%." + rounding + "f", purchaseRate));

            }
        }
        log.info(result);
        return result;
    }
}
