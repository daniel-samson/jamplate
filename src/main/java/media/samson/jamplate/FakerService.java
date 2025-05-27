package media.samson.jamplate;

import net.datafaker.Faker;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Service class for generating fake data using the Datafaker library.
 * Provides various types of fake data that can be used for template variables.
 */
public class FakerService {
    
    private final Faker faker;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    /**
     * Creates a new FakerService with default locale.
     */
    public FakerService() {
        this.faker = new Faker();
    }
    
    /**
     * Creates a new FakerService with specified locale.
     * 
     * @param locale The locale to use for generating fake data
     */
    public FakerService(Locale locale) {
        this.faker = new Faker(locale);
    }
    
    /**
     * Gets all available faker data types.
     * 
     * @return List of available data types
     */
    public static List<String> getAvailableTypes() {
        return Arrays.asList(
            // Basic types
            "Text",
            "Lorem Text",
            "Sentence",
            "Paragraph",
            
            // Personal information
            "First Name",
            "Last Name",
            "Full Name",
            "Username",
            "Email",
            "Phone Number",
            "Date of Birth",
            "Age",
            "Gender",
            
            // Address information
            "Street Address",
            "City",
            "State",
            "Country",
            "Zip Code",
            "Full Address",
            
            // Company information
            "Company Name",
            "Job Title",
            "Department",
            "Industry",
            
            // Internet & Technology
            "Domain Name",
            "URL",
            "IP Address",
            "MAC Address",
            "UUID",
            "Password",
            
            // Financial
            "Credit Card Number",
            "Bank Account",
            "Currency Code",
            "Price",
            
            // Date & Time
            "Date",
            "Time",
            "DateTime",
            "Future Date",
            "Past Date",
            
            // Numbers
            "Number",
            "Decimal",
            "Percentage",
            
            // Colors & Design
            "Color Name",
            "Hex Color",
            "RGB Color",
            
            // Food & Commerce
            "Product Name",
            "Brand",
            "Food Item",
            "Beer Name",
            
            // Entertainment
            "Book Title",
            "Movie Title",
            "Music Genre",
            "Band Name",
            
            // Animals & Nature
            "Animal",
            "Dog Breed",
            "Cat Breed",
            "Tree",
            
            // Education
            "University",
            "Course",
            "Grade",
            
            // Miscellaneous
            "Quote",
            "Hashtag",
            "File Name",
            "File Extension"
        );
    }
    
    /**
     * Generates fake data based on the specified type.
     * 
     * @param type The type of fake data to generate
     * @return Generated fake data as a string
     */
    public String generateFakeData(String type) {
        if (type == null) {
            return "";
        }
        
        try {
            return switch (type) {
                // Basic types
                case "Text" -> faker.lorem().word();
                case "Lorem Text" -> String.join(" ", faker.lorem().words(3));
                case "Sentence" -> faker.lorem().sentence();
                case "Paragraph" -> faker.lorem().paragraph();
                
                // Personal information
                case "First Name" -> faker.name().firstName();
                case "Last Name" -> faker.name().lastName();
                case "Full Name" -> faker.name().fullName();
                case "Username" -> faker.internet().username();
                case "Email" -> faker.internet().emailAddress();
                case "Phone Number" -> faker.phoneNumber().phoneNumber();
                case "Date of Birth" -> {
                    LocalDate birthDate = LocalDate.now().minusYears(faker.number().numberBetween(18, 80));
                    yield birthDate.format(dateFormatter);
                }
                case "Age" -> String.valueOf(faker.number().numberBetween(18, 80));
                case "Gender" -> faker.demographic().sex();
                
                // Address information
                case "Street Address" -> faker.address().streetAddress();
                case "City" -> faker.address().city();
                case "State" -> faker.address().state();
                case "Country" -> faker.address().country();
                case "Zip Code" -> faker.address().zipCode();
                case "Full Address" -> faker.address().fullAddress();
                
                // Company information
                case "Company Name" -> faker.company().name();
                case "Job Title" -> faker.job().title();
                case "Department" -> faker.commerce().department();
                case "Industry" -> faker.company().industry();
                
                // Internet & Technology
                case "Domain Name" -> faker.internet().domainName();
                case "URL" -> faker.internet().url();
                case "IP Address" -> faker.internet().ipV4Address();
                case "MAC Address" -> faker.internet().macAddress();
                case "UUID" -> faker.internet().uuid();
                case "Password" -> faker.internet().password();
                
                // Financial
                case "Credit Card Number" -> faker.finance().creditCard();
                case "Bank Account" -> faker.finance().iban();
                case "Currency Code" -> faker.currency().name();
                case "Price" -> "$" + faker.commerce().price();
                
                // Date & Time
                case "Date" -> {
                    LocalDate randomDate = LocalDate.now().minusDays(faker.number().numberBetween(0, 365));
                    yield randomDate.format(dateFormatter);
                }
                case "Time" -> {
                    int hour = faker.number().numberBetween(0, 23);
                    int minute = faker.number().numberBetween(0, 59);
                    int second = faker.number().numberBetween(0, 59);
                    yield String.format("%02d:%02d:%02d", hour, minute, second);
                }
                case "DateTime" -> {
                    LocalDate randomDate = LocalDate.now().minusDays(faker.number().numberBetween(0, 365));
                    int hour = faker.number().numberBetween(0, 23);
                    int minute = faker.number().numberBetween(0, 59);
                    yield randomDate.format(dateFormatter) + " " + String.format("%02d:%02d:00", hour, minute);
                }
                case "Future Date" -> {
                    LocalDate futureDate = LocalDate.now().plusDays(faker.number().numberBetween(1, 365));
                    yield futureDate.format(dateFormatter);
                }
                case "Past Date" -> {
                    LocalDate pastDate = LocalDate.now().minusDays(faker.number().numberBetween(1, 365));
                    yield pastDate.format(dateFormatter);
                }
                
                // Numbers
                case "Number" -> String.valueOf(faker.number().numberBetween(1, 1000));
                case "Decimal" -> String.format("%.2f", faker.number().randomDouble(2, 1, 1000));
                case "Percentage" -> faker.number().numberBetween(0, 100) + "%";
                
                // Colors & Design
                case "Color Name" -> faker.color().name();
                case "Hex Color" -> faker.color().hex();
                case "RGB Color" -> "rgb(" + faker.number().numberBetween(0, 255) + ", " + 
                                   faker.number().numberBetween(0, 255) + ", " + 
                                   faker.number().numberBetween(0, 255) + ")";
                
                // Food & Commerce
                case "Product Name" -> faker.commerce().productName();
                case "Brand" -> faker.commerce().brand();
                case "Food Item" -> faker.food().ingredient();
                case "Beer Name" -> faker.beer().name();
                
                // Entertainment
                case "Book Title" -> faker.book().title();
                case "Movie Title" -> faker.name().fullName() + " Movie";
                case "Music Genre" -> faker.music().genre();
                case "Band Name" -> faker.rockBand().name();
                
                // Animals & Nature
                case "Animal" -> faker.animal().name();
                case "Dog Breed" -> faker.dog().breed();
                case "Cat Breed" -> faker.cat().breed();
                case "Tree" -> faker.lorem().word() + " Tree";
                
                // Education
                case "University" -> faker.university().name();
                case "Course" -> faker.educator().course();
                case "Grade" -> faker.number().numberBetween(1, 12) + "th Grade";
                
                // Miscellaneous
                case "Quote" -> faker.shakespeare().hamletQuote();
                case "Hashtag" -> "#" + faker.lorem().word();
                case "File Name" -> faker.file().fileName();
                case "File Extension" -> faker.file().extension();
                
                // Default fallback
                default -> faker.lorem().word();
            };
        } catch (Exception e) {
            // If any faker method fails, return a simple fallback
            System.err.println("Error generating fake data for type '" + type + "': " + e.getMessage());
            return faker.lorem().word();
        }
    }
    
    /**
     * Gets a sample of fake data for the specified type (for preview purposes).
     * 
     * @param type The type of fake data
     * @return A sample of the fake data
     */
    public String getSampleData(String type) {
        return generateFakeData(type);
    }
} 