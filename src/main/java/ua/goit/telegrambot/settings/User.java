package ua.goit.telegrambot.settings;

import lombok.Data;
import ua.goit.telegrambot.api.CurrencyJsonUpdate;
import ua.goit.telegrambot.api.dto.BankNAME;
import ua.goit.telegrambot.api.dto.Currency;

//TODO telegram bot API has own User class -> please rename this entity to BotUser or smth
@Data
public class User {
    private final long id;
    private BankNAME bank;
    private Currency currency;
    private int rounding;
    private boolean scheduler;
    private int schedulerTime;
    private boolean isEnglish;
    private boolean isUkrainian;
    //TODO currency field is missing, need to be ENUM or CONSTANT

    public int getSchedulerTime() {
        return schedulerTime;
    }

    public void setSchedulerTime(int schedulerTime) {
        this.schedulerTime = schedulerTime;
    }

    public User(long id) {
        this.id = id;
        bank = BankNAME.NBU;
        currency = Currency.USD;
        rounding = 2;
        scheduler = true;
        schedulerTime = 9;
        isEnglish = true;
        isUkrainian = false;
    }

}
