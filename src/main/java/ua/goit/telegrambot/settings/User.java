package ua.goit.telegrambot.settings;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ua.goit.telegrambot.api.dto.BankNAME;
import ua.goit.telegrambot.api.dto.Currency;

//TODO telegram bot API has own User class -> please rename this entity to BotUser or smth
@Data
@Entity
@Table(name = "users")
@ToString
@EqualsAndHashCode
public class User {
    @Id
    @Column(name = "chat_id")
    private Long id;
    @Column(name = "bank")
    @Enumerated(EnumType.STRING)
    private BankNAME bank;
    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private Currency currency;
    @Column(name = "rounding")
    private int rounding;
    @Column(name = "scheduler")
    private boolean scheduler;
    @Column(name = "schedulerTime")
    private int schedulerTime;
    @Column(name = "isEnglish")
    private boolean isEnglish;
    @Column(name = "isUkrainian")
    private boolean isUkrainian;

    public int getSchedulerTime() {
        return schedulerTime;
    }

    public void setSchedulerTime(int schedulerTime) {
        this.schedulerTime = schedulerTime;
    }
    public User() {

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
