package ua.goit.telegrambot.settings;

import lombok.Data;
import ua.goit.telegrambot.api.CurrencyJsonUpdate;
import ua.goit.telegrambot.api.dto.BankNAME;

//TODO telegram bot API has own User class -> please rename this entity to BotUser or smth
@Data
public class User {// no TODO's in master branch
    private final long id;
    private BankNAME bank; //TODO we have to make it ENUM or CONSTANT
    private boolean usd;
    private boolean eur;
    private boolean gbp;
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
        usd = true;
        eur = false;
        gbp = false;
        rounding = 2;
        scheduler = true;
        schedulerTime = 9;
        isEnglish = true;
        isUkrainian = false;
    }

}
