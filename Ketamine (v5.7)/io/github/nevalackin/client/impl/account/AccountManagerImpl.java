package io.github.nevalackin.client.impl.account;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.nevalackin.client.api.account.Account;
import io.github.nevalackin.client.api.account.AccountManager;
import io.github.nevalackin.client.api.file.FileManager;
import io.github.nevalackin.client.impl.core.KetamineClient;
import io.github.nevalackin.client.impl.event.game.WatchdogBannedEvent;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Session;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class AccountManagerImpl implements AccountManager {

    private final List<Account> accounts = new ArrayList<>();

    public AccountManagerImpl() {
        KetamineClient.getInstance().getEventBus().subscribe(this);
    }

    @EventLink
    private final Listener<WatchdogBannedEvent> onWatchdogBan = event -> {
        final IChatComponent reason = event.getReason();
        final String text = reason.getUnformattedText();

        final String[] lines = text.split("\n");
        if (lines.length != 7) return;
        final String firstLine = lines[0];
        if (firstLine.equals("You are permanently banned from this server!")) {
            final Session currentSession = Minecraft.getMinecraft().getSession();

            if (currentSession != null) {
                final Account currentAccount = this.getAccountByUsername(currentSession.getUsername());
                if (currentAccount != null) {
                    currentAccount.setUnbanDate(-1337); // Security alert
                }
            }
        } else if (firstLine.startsWith("You are temporarily banned for") && firstLine.endsWith("from this server!")) {
            final String banLengthStr = firstLine.substring(31).replace(" from this server!", "");
            final SimpleDateFormat hypixelDateFormat = new SimpleDateFormat("D'd' H'h' m'm' s's'");
            try {
                final long banTimeRemaining = hypixelDateFormat.parse(banLengthStr).getTime();
                final Session currentSession = Minecraft.getMinecraft().getSession();

                if (currentSession != null) {
                    final Account currentAccount = this.getAccountByUsername(currentSession.getUsername());
                    if (currentAccount != null) {
                        currentAccount.setUnbanDate(System.currentTimeMillis() + banTimeRemaining);
                    }
                }
            } catch (ParseException ignored) {
//                System.out.println("Unable to parse: " + banLengthStr);
            }
        }
    };

    private Account getAccountByUsername(final String username) {
        for (final Account account : this.getAccounts()) {
            if (account.getUsername().equals(username)) {
                return account;
            }
        }

        return null;
    }

    @Override
    public void load() {
        final FileManager fileManager = KetamineClient.getInstance().getFileManager();
        final File accountsFile = fileManager.getFile("alts");
        final JsonElement array = fileManager.parse(accountsFile);

        if (array.isJsonArray()) {
            for (final JsonElement element : array.getAsJsonArray()) {
                final JsonObject object = element.getAsJsonObject();
                this.addAccount(Account.from(object));
            }
        }
    }

    @Override
    public void save() {
        final FileManager fileManager = KetamineClient.getInstance().getFileManager();
        final File accountsFile = fileManager.getFile("alts");

        final JsonArray accountsArray = new JsonArray();

        for (final Account account : this.getAccounts()) {
            accountsArray.add(account.save());
        }

        fileManager.writeJson(accountsFile, accountsArray);
    }

    @Override
    public void addAccount(Account account) {
        this.accounts.add(account);
    }

    @Override
    public void deleteAccount(Account account) {
        this.accounts.remove(account);
    }

    @Override
    public Collection<Account> getAccounts() {
        return this.accounts;
    }

    @Override
    public Collection<Account> getWorkingAccounts() {
        return this.accounts.stream()
            .filter(account -> account.isWorking() || account.getUsername().length() == 0)
            .collect(Collectors.toList());
    }

    @Override
    public Collection<Account> getUnbannedAccounts() {
        return this.getWorkingAccounts().stream()
            .filter(account -> !account.isBanned())
            .collect(Collectors.toList());
    }
}
