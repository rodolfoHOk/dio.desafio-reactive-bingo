package me.dio.hiokdev.reactive_bingo.domain.dto;

import me.dio.hiokdev.reactive_bingo.domain.models.BingoCard;
import me.dio.hiokdev.reactive_bingo.domain.models.Player;
import me.dio.hiokdev.reactive_bingo.domain.models.Round;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record MailMessage(
        String destination,
        String subject,
        String template,
        Map<String, Object> variables
) {

    public static MailMessage create(final Round round, final Player player, final BingoCard bingoCard) {
        return MailMessage.builder()
                .create(round, player, bingoCard)
                .template("mail/roundResult")
                .build();
    }

    public static MailMessage createWinner(final Round round, final Player player, final BingoCard bingoCard) {
        return MailMessage.builder()
                .create(round, player, bingoCard)
                .template("mail/winnerResult")
                .build();
    }

    public static MailMessageBuilder builder() {
        return new MailMessageBuilder();
    }

    public static class MailMessageBuilder {

        private String destination;
        private String subject;
        private String template;
        private Map<String, Object> variables = new HashMap<>();

        public MailMessageBuilder create(final Round round, final Player player, final BingoCard bingoCard) {
            return this.destination(player.email())
                    .subject("Reactive Bingo: Ganhador")
                    .playerName(player.name())
                    .roundId(round.id())
                    .cardNumbers(bingoCard.numbers())
                    .sortedNumbers(round.sortedNumbers())
                    .hintCount(bingoCard.hintCount());
        }

        public MailMessageBuilder destination(final String destination) {
            this.destination = destination;
            return this;
        }

        public MailMessageBuilder subject(final String subject) {
            this.subject = subject;
            return this;
        }

        public MailMessageBuilder template(final String template) {
            this.template = template;
            return this;
        }

        public MailMessageBuilder playerName(final String playerName) {
            return variable("playerName", playerName);
        }

        public MailMessageBuilder roundId(final String roundId) {
            return variable("roundId", roundId);
        }

        public MailMessageBuilder cardNumbers(final List<Integer> cardNumbers) {
            cardNumbers.sort(Integer::compareTo);
            return variable("cardNumbers", StringUtils.join(cardNumbers, ","));
        }

        public MailMessageBuilder sortedNumbers(final List<Integer> sortedNumbers) {
            sortedNumbers.sort(Integer::compareTo);
            return variable("sortedNumbers", StringUtils.join(sortedNumbers, ","));
        }

        public MailMessageBuilder hintCount(final Integer hintCount) {
            return variable("hintCount", hintCount);
        }

        public MailMessage build() {
            return new MailMessage(destination, subject, template, variables);
        }

        private MailMessageBuilder variable(final String key, final Object value) {
            this.variables.put(key, value);
            return this;
        }

    }

}
