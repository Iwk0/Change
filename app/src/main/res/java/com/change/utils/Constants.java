package java.com.change.utils;

public interface Constants {

    int DATABASE_VERSION = 10;
    String DATABASE_NAME = "Change";
    String TABLE_RATES = "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s INTEGER, %s REAL, %s REAL, %s TEXT);";
    String TABLE_TRANSACTIONS = "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s REAL, %s TEXT, %s REAL, %s TEXT);";
    String TABLE_STOCKS = "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s REAL);";
    String TABLE_MOVEMENT = "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT)";
    String ROW = "ROW";
    String NAME = "NAME_";
    String CODE = "CODE";
    String RATIO = "RATIO";
    String REVERSE_RATE = "REVERSERATE";
    String RATE = "RATE";
    String RATES = "rates";
    String TABLE_NAME_RATES = "rates";
    String TABLE_NAME_LOGS = "logs";
    String TABLE_NAME_STOCKS = "stocks";
    String TABLE_NAME_MOVEMENT = "movement";
    String DATE = "date";
    String CODE_FROM = "codeFrom";
    String CURRENCY_FROM_AMOUNT = "currencyFromAmount";
    String CODE_TO = "codeTo";
    String CURRENCY_TO_AMOUNT = "currencyToAmount";
    String RATE_NAME = "name";
    String STOCK = "stock";
    String ID = "id";
    String LOG = "log";
    String MOVEMENT = "movement";
}