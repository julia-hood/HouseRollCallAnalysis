import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import java.util.Scanner;

/**
 * Prompts the user to input two years between 1953 and 2024 to determine their statistical differences
 * in House of Representatives polarization based on roll call data, controlling for near-unanimous votes.
 */
public class RollCallAnalysis {
  private int year1;
  private int year2;

  /**
   * Initializes analyzer and executes terminal interaction.
   */
  public static void main(String[] args) {
    System.out.println("Welcome to the House of Representatives Polarization Analyzer!");
    System.out.println("Please enter two years between 1953 and 2024 to compare polarization levels.");
    System.out.println("The first year must precede the second, so you cannot start with 2024.");
    System.out.println("Type 'q' at any point to exit the program.");
    new RollCallAnalysis().execute();
  }

  // establishes RConnection and performs the terminal interaction and regression
  private void execute() {
    try {
      RConnection connection = new RConnection();
      Scanner scanner = new Scanner(System.in);

      this.promptUser(scanner);
      this.regression(connection);

    } catch (RserveException e) {
      System.err.println("Failed to connect to Rserve: " + e.getMessage());
    } catch (REXPMismatchException e) {
      System.err.println("Error with R syntax: " + e.getMessage());
    }
  }

  private void promptUser(Scanner scanner) {
    this.year1 = getValidYear(scanner, "Enter the first year: ", 1953, 2023);
    this.year2 = getValidYear(scanner, "Enter the second year (must be after " + year1 + "): ",
            year1 + 1, 2024);
    System.out.println("Calculating regression. Please wait...");
  }

  private int getValidYear(Scanner scanner, String prompt, int min, int max) {
    int year;
    while (true) {
      System.out.print(prompt);
      if (scanner.hasNextInt()) {
        year = scanner.nextInt();
        if (year >= min && year <= max) {
          return year;
        } else {
          System.out.println("Invalid input. Enter a year between " + min + " and " + max + ".");
        }
      } else {
        String input = scanner.next();
        if (input.equalsIgnoreCase("q")) {
          System.out.println("Exiting program...");
          System.exit(0);
        } else {
          System.out.println("Invalid input. Please enter a valid year.");
        }
      }
    }
  }

  // perform regression analysis
  private void regression(RConnection connection) throws RserveException, REXPMismatchException {

    // Load csv into dataframe and rename columns
    connection.eval("df <- read.csv('https://austinclemens.com/rohde_rollcalls/house_votes.csv');");
    connection.eval("colnames(df)[colnames(df) == 'v16'] <- 'party_unity_vote'");
    connection.eval("colnames(df)[colnames(df) == 'v18'] <- 'near_unanimous'");

    // filters dataset for the two chosen years
    connection.eval("subset <- subset(df, year == " + year1 + " | year == " + year2 + ")");

    // add binary variable to represent the first year as 1 and second year as 0
    connection.eval("subset$is_year1 <- ifelse(subset$year == " + year1 + ", 1, 0)");

    // performs regression on the presence of a party unity vote, controlling for near unanimous votes
    connection.eval("model <- lm(party_unity_vote ~ is_year1 + near_unanimous, data = subset)");

    // outputs summary of regression
    String[] summary = connection.eval("capture.output(summary(model))").asStrings();
    for (String line : summary) {
      System.out.println(line);
    }

    // Extract p-value and t-value of is_year1
    double yearPValue = connection.eval("summary(model)$coefficients[2,4]").asDouble();
    double yearTValue = connection.eval("summary(model)$coefficients[2,3]").asDouble();
    System.out.println(evaluateResults(yearPValue, yearTValue));

    connection.close();
  }

  // writes an overview message of the results based on the regression
  private String evaluateResults(double pYear, double tYear) {
    return "This regression finds that the difference in polarization was " + evaluateSignificance(pYear)
            + " significant,\nwith " + evaluateChange(tYear) + " polarization in "
            + year2 + " compared to " + year1 + ".";
  }

  // evaluates the p value and returns to what extent of significance it is
  private String evaluateSignificance(double pValue) {
    if (pValue > 0.1) {
      return "not";
    } else if (pValue > 0.05) {
      return "marginally";
    } else if (pValue > 0.01) {
      return "";
    } else if (pValue > 0.001) {
      return "very";
    } else  {
      return "highly";
    }
  }

  // evaluates the t value to determine if the data was increasing or decreasing
  private String evaluateChange(double tValue) {
    if (tValue > 0.0) {
      return "decreased"; // is_year1 is positive, so there was more polarization in the earlier year
    } else if (tValue < 0.0) {
      return "increased"; // is_year1 is negative, so there was more polarization in the later year
    } else {
      return "unchanged";
    }
  }
}