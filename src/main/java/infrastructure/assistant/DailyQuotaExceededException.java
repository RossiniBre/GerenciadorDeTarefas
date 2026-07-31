package infrastructure.assistant;

public class DailyQuotaExceededException extends RuntimeException {
    public DailyQuotaExceededException() {
        super("Limite diário de uso foi atingido. Tente novamente amanhã.");
    }
}