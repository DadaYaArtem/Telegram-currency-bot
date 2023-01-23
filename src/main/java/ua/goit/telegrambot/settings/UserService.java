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

    public boolean getUkrainian(long userId) {
        return userStorage.get(userId).isUkrainian();
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
        return MessageFormat
                .format("{0} exchange rate: {1}\n Purchase: {2}\n Sale: ⏳",
                        user.getBank(), currencyPair, String.format("%." + user.getRounding() + "f", purchaseRate));
    }

    private String getRateAndFormatBuySell(long userId, String operation1, String operation2, CurrencyService currencyService, String currencyPair) {
        User user = userStorage.get(userId);
        double buyRate = currencyService.getRate(user.getCurrency()).get(operation1);
        double saleRate = currencyService.getRate(user.getCurrency()).get(operation2);
        return MessageFormat
                .format("{0} exchange rate: {1}\n Purchase: {2}\n Sale: {3}",
                        user.getBank(), currencyPair, String.format("%." + user.getRounding() + "f", buyRate), String.format("%." + user.getRounding() + "f", saleRate));
    }

    public String getInfoUkr(long userId) {
        BankNAME bank = getBank(userId);
        Currency currency = getCurrency(userId);
        int rounding = getRounding(userId);
        String result = "";
        String currencyPairUsd = "UAH/USD";
        String currencyPairEur = "UAH/EUR";
        String currencyPairGbp = "UAH/GBP";

        if (bank == BankNAME.NBU) {

            if (currency == Currency.USD) {
                double purchaseRate1 = nbuCurrencyService.getRate(Currency.USD).get("rateUSD");
                double purchaseRate = Precision.round(purchaseRate1, rounding);
                result = MessageFormat
                        .format("{0} курс валют: {1}\n купівля: {2}\n продаж: ⏳ ", "НБУ", currencyPairUsd, String.format("%." + rounding + "f", purchaseRate));

            }
            if (currency == Currency.EUR) {
                double purchaseRate1 = nbuCurrencyService.getRate(Currency.EUR).get("rateEUR");
                double purchaseRate = Precision.round(purchaseRate1, rounding);
                result = MessageFormat
                        .format("{0} курс валют: {1}\n купівля: {2}\n продаж: ⏳ ", "НБУ", currencyPairEur, String.format("%." + rounding + "f", purchaseRate));

            }
            if (currency == Currency.GBP) {
                double purchaseRate1 = nbuCurrencyService.getRate(Currency.GBP).get("rateGBP");
                double purchaseRate = Precision.round(purchaseRate1, rounding);
                result = MessageFormat
                        .format("{0} курс валют: {1}\n купівля: {2}\n продаж: ⏳ ", "НБУ", currencyPairGbp, String.format("%." + rounding + "f", purchaseRate));

            }
        }

        if (bank == BankNAME.MONO) {
            if (currency == Currency.USD) {
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
            if (currency == Currency.EUR) {
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
            if (currency == Currency.GBP) {
                double purchaseRate1 = monoCurrencyService.getRate(Currency.GBP).get("crossGBP");
                double purchaseRate = Precision.round(purchaseRate1, rounding);
                result = MessageFormat
                        .format("{0} курс валют: {1}\n купівля: {2}\n продаж: ⏳ ", "МоноБанк", currencyPairGbp, String.format("%." + rounding + "f", purchaseRate));

            }
        }

        if (bank == BankNAME.PRIVAT) {
            if (currency == Currency.USD) {
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
            if (currency == Currency.EUR) {
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
            if (currency == Currency.GBP) {
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
