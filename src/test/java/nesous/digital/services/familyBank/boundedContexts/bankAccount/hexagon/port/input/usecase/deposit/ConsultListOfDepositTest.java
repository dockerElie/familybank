package nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.input.usecase.deposit;


import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.*;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.model.DepositFilter;
import nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.port.output.providers.DepositProvider;
import nesous.digital.services.familyBank.boundedContexts.shareKernel.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Month;
import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.Deposit.DepositBuilder.depositBuilder;
import static nesous.digital.services.familyBank.boundedContexts.bankAccount.hexagon.domain.value.object.Status.*;
import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;
public class ConsultListOfDepositTest {

    private final DepositProvider depositProvider = createMock(DepositProvider.class);

    private ConsultListOfDeposit consultListOfDeposit;

    private static final String DEPOSIT_NAME = "deposit";
    private static final DepositMoney money = DepositMoney.of(Money.of(100));

    private static final Integer CURRENT_DEPOSIT_YEAR = Year.now().getValue();
    private static final DepositIdentifier depositIdentifier = DepositIdentifier.generate();
    private static final DepositIdentifier depositIdentifier1 = DepositIdentifier.generate();
    private static Stream<Arguments> defaultList_whenNoDepositHasBeenDoneInTheCurrentYear_thenReturnEmptyList() {

        // Arrange
        Date expirationDate = new Date(13, Month.APRIL, CURRENT_DEPOSIT_YEAR + 1);
        Date dateForLastYear = new Date(1, Month.JANUARY, CURRENT_DEPOSIT_YEAR - 1);

        Deposit deposit = depositBuilder()

                .withName(DepositName.of(new Text(DEPOSIT_NAME)))
                .withDescription(new Description(new Text(DEPOSIT_NAME)))
                .withDate(dateForLastYear)
                .withExpirationDate(ExpirationDate.of(expirationDate))
                .withIdentifier(depositIdentifier)
                .withMoney(money)
                .withStatus(ACTIVATED)
                .withUserExpirationDate(new UserExpirationDate(expirationDate))
                .build();
        AccountId accountId = AccountId.of("A-eret-2024");
        return Stream.of(Arguments.of(Collections.singletonList(deposit), accountId));
    }


    private static Stream<Arguments> defaultList_whenDepositHasBeenDenied_thenReturnListOfDepositForCurrentYear() {

        // Arrange
        Date expirationDate = new Date(13, Month.APRIL, CURRENT_DEPOSIT_YEAR);
        Date dateForCurrentYear = new Date(1, Month.JANUARY, CURRENT_DEPOSIT_YEAR );
        Deposit deposit = depositBuilder()

                .withName(DepositName.of(new Text(DEPOSIT_NAME)))
                .withDescription(new Description(new Text(DEPOSIT_NAME)))
                .withDate(dateForCurrentYear)
                .withExpirationDate(ExpirationDate.of(expirationDate))
                .withIdentifier(depositIdentifier)
                .withMoney(money)
                .withStatus(DENIED)
                .withReason(new Reason(new Text("denied")))
                .build();

        AccountId accountId = AccountId.of("A-ert-2024");

        return Stream.of(Arguments.of(Collections.singletonList(deposit), accountId));
    }

    private static Stream<Arguments> defaultList_whenUserDidNotSetExpirationDate_thenReturnListOfDepositForCurrentYear() {

        // Arrange
        Date expirationDate = new Date(13, Month.APRIL, CURRENT_DEPOSIT_YEAR);
        Date dateForCurrentYear = new Date(1, Month.JANUARY, CURRENT_DEPOSIT_YEAR );
        Deposit deposit = depositBuilder()

                .withName(DepositName.of(new Text(DEPOSIT_NAME)))
                .withDescription(new Description(new Text(DEPOSIT_NAME)))
                .withDate(dateForCurrentYear)
                .withExpirationDate(ExpirationDate.of(expirationDate))
                .withIdentifier(depositIdentifier)
                .withMoney(money)
                .withStatus(ACTIVATED)
                .build();

        return Stream.of(Arguments.of(Collections.singletonList(deposit), AccountId.of("A-ert-2024")));
    }

    private static Stream<Arguments> defaultList_whenDepositHasBeenDone_thenReturnListOfDepositSortedInReverseOrderBaseOnDepositDate() {

        // Arrange
        Date expirationDate = new Date(15, Month.APRIL, CURRENT_DEPOSIT_YEAR);
        Date dateForCurrentYear = new Date(5, Month.JANUARY, CURRENT_DEPOSIT_YEAR);
        String DEPOSIT_NAME_1 = "deposit1";
        Deposit deposit = depositBuilder()

                .withName(DepositName.of(new Text(DEPOSIT_NAME)))
                .withDescription(new Description(new Text(DEPOSIT_NAME)))
                .withDate(dateForCurrentYear)
                .withExpirationDate(ExpirationDate.of(expirationDate))
                .withIdentifier(depositIdentifier)
                .withMoney(money)
                .withStatus(VALIDATED)
                .build();


        Deposit deposit1 = depositBuilder()

                .withName(DepositName.of(new Text(DEPOSIT_NAME_1)))
                .withDescription(new Description(new Text(DEPOSIT_NAME_1)))
                .withDate(new Date(5, Month.MARCH, CURRENT_DEPOSIT_YEAR))
                .withExpirationDate(ExpirationDate.of(expirationDate))
                .withIdentifier(depositIdentifier1)
                .withMoney(money)
                .withStatus(ACTIVATED)
                .build();

        return Stream.of(Arguments.of(Arrays.asList(deposit, deposit1), AccountId.of("A-ert-2024")));
    }

    private static Stream<Arguments> searchDepositInSpecificStatuses_GivenListOfTheDeposit_thenReturnResults() {

        // Arrange
        Date expirationDate = new Date(13, Month.APRIL, CURRENT_DEPOSIT_YEAR);
        Date dateForCurrentYear = new Date(1, Month.JANUARY, CURRENT_DEPOSIT_YEAR );
        String DEPOSIT_NAME_1 = "deposit";
        Deposit deposit = depositBuilder()

                .withName(DepositName.of(new Text(DEPOSIT_NAME)))
                .withDescription(new Description(new Text(DEPOSIT_NAME)))
                .withDate(dateForCurrentYear)
                .withExpirationDate(ExpirationDate.of(expirationDate))
                .withIdentifier(depositIdentifier)
                .withMoney(money)
                .withStatus(VALIDATED)
                .build();


        Deposit deposit1 = depositBuilder()

                .withName(DepositName.of(new Text(DEPOSIT_NAME_1)))
                .withDescription(new Description(new Text(DEPOSIT_NAME_1)))
                .withDate(dateForCurrentYear)
                .withExpirationDate(ExpirationDate.of(expirationDate))
                .withIdentifier(depositIdentifier1)
                .withMoney(money)
                .withStatus(ACTIVATED)
                .build();

        return Stream.of(Arguments.of(Arrays.asList(deposit, deposit1), Arrays.asList(ACTIVATED, DONE), AccountId.of("A-zzd-2024")));
    }

    private static Stream<Arguments> searchDepositContainingSpecificIdentifier_GivenListOfDeposit_thenReturnResults() {

        // Arrange
        Date expirationDate = new Date(15, Month.APRIL, CURRENT_DEPOSIT_YEAR);
        Date dateForCurrentYear = new Date(5, Month.JANUARY, CURRENT_DEPOSIT_YEAR);
        String depositName = "depositElie";
        String depositAnotherName = "depositEla";
        Deposit deposit = depositBuilder()

                .withName(DepositName.of(new Text(depositName)))
                .withDescription(new Description(new Text(depositName)))
                .withDate(dateForCurrentYear)
                .withExpirationDate(ExpirationDate.of(expirationDate))
                .withIdentifier(depositIdentifier)
                .withMoney(money)
                .withStatus(VALIDATED)
                .build();


        Deposit deposit1 = depositBuilder()

                .withName(DepositName.of(new Text(depositAnotherName)))
                .withDescription(new Description(new Text(depositAnotherName)))
                .withDate(new Date(5, Month.MARCH, CURRENT_DEPOSIT_YEAR - 3))
                .withExpirationDate(ExpirationDate.of(expirationDate))
                .withIdentifier(DepositIdentifier.generateForASpecificYear(Year.of(CURRENT_DEPOSIT_YEAR - 3)))
                .withMoney(money)
                .withStatus(ACTIVATED)
                .build();

        return Stream.of(Arguments.of(Arrays.asList(deposit, deposit1), AccountId.of("A-233-zuie")));
    }

    private static Stream<Arguments> searchDepositWithSpecificStatusBetweenDates_GivenDepositStatusAndStartAndEndDate_thenReturnResult() {

        // Arrange
        Date expirationDate = new Date(15, Month.APRIL, CURRENT_DEPOSIT_YEAR);
        Date dateForCurrentYear = new Date(5, Month.JANUARY, CURRENT_DEPOSIT_YEAR);
        String depositName = "depositElie";
        Deposit deposit = depositBuilder()

                .withName(DepositName.of(new Text(depositName)))
                .withDescription(new Description(new Text(depositName)))
                .withDate(dateForCurrentYear)
                .withExpirationDate(ExpirationDate.of(expirationDate))
                .withIdentifier(depositIdentifier)
                .withMoney(money)
                .withStatus(VALIDATED)
                .build();

        return Stream.of(Arguments.of(Collections.singletonList(deposit), AccountId.of("1-233-2024")));
    }

    @BeforeEach
    public void setUp() {
        consultListOfDeposit = new ConsultListOfDeposit(depositProvider);
    }

    @Test
    @DisplayName("No deposit has been done")
    public void build_whenNoDepositHasBeenDone_thenReturnEmptyList() {

        // Arrange
        AccountId accountId = AccountId.of("A-sdsd-2023");
        expect(depositProvider.of(accountId)).andReturn(Collections.emptyList());

        // Act
        replay(depositProvider);
        List<Deposit> defaultList = consultListOfDeposit.withDefaultList(accountId);

        // Assert
        assertTrue(defaultList.isEmpty());
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("Deposit were not done in the current year")
    public void defaultList_whenNoDepositHasBeenDoneInTheCurrentYear_thenReturnEmptyList(
            List<Deposit> depositList, AccountId accountId) {

        // Arrange
        expect(depositProvider.of(accountId)).andReturn(depositList);

        // Act
        replay(depositProvider);
        List<Deposit> defaultList = consultListOfDeposit.withDefaultList(accountId);

        // Assert
        assertNotNull(defaultList);
        assertTrue(defaultList.isEmpty());
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("by default return list of deposit of the current year with denied ones")
    public void defaultList_whenDepositHasBeenDenied_thenReturnListOfDepositForCurrentYear(List<Deposit> depositList,
                                                                                           AccountId accountId) {
        // Arrange
        expect(depositProvider.of(accountId)).andReturn(depositList);

        // Act
        replay(depositProvider);
        List<Deposit> defaultList = consultListOfDeposit.withDefaultList(accountId);
        Deposit deposit = defaultList.stream().filter(x -> Objects.equals(x.getDate().year(), CURRENT_DEPOSIT_YEAR)).findAny().get();

        // Assert
        assertEquals(Year.now().getValue(), deposit.getDate().year());
        assertEquals(depositIdentifier.value(), deposit.getIdentifier().value());
        assertEquals(DEPOSIT_NAME, deposit.getName().text().value());
        assertEquals(money.value(), deposit.getMoney().value());
        assertEquals(DENIED, deposit.getStatus());
        assertEquals(Month.APRIL, deposit.getExpirationDate().date().month());
        assertEquals(13, deposit.getExpirationDate().date().day());
        assertEquals(CURRENT_DEPOSIT_YEAR, deposit.getExpirationDate().date().year());
        assertNull(deposit.getUserExpirationDate());
        assertNotNull(deposit.getReason());
        assertTrue(defaultList.stream().allMatch(x -> Objects.equals(x.getDate().year(), CURRENT_DEPOSIT_YEAR)));
        assertTrue(isSortedDescendingOrderBasedOnDepositDate(defaultList));
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("by default return list of deposit of the current year with empty user expiration date")
    public void defaultList_whenUserDidNotSetExpirationDate_thenReturnListOfDepositForCurrentYear(List<Deposit> depositList, AccountId accountId) {

        // Arrange
        expect(depositProvider.of(accountId)).andReturn(depositList);

        // Act
        replay(depositProvider);
        List<Deposit> defaultList = consultListOfDeposit.withDefaultList(accountId);
        Deposit deposit = defaultList.stream().filter(x -> Objects.equals(x.getDate().year(), CURRENT_DEPOSIT_YEAR)).findAny().get();

        // Assert
        assertEquals(Year.now().getValue(), deposit.getDate().year());
        assertEquals(depositIdentifier.value(), deposit.getIdentifier().value());
        assertEquals(DEPOSIT_NAME, deposit.getName().text().value());
        assertEquals(money.value(), deposit.getMoney().value());
        assertEquals(ACTIVATED, deposit.getStatus());
        assertEquals(Month.APRIL, deposit.getExpirationDate().date().month());
        assertEquals(13, deposit.getExpirationDate().date().day());
        assertEquals(CURRENT_DEPOSIT_YEAR, deposit.getExpirationDate().date().year());

        assertNull(deposit.getUserExpirationDate());
        assertNull(deposit.getReason());
        assertTrue(defaultList.stream().allMatch(x -> Objects.equals(x.getDate().year(), CURRENT_DEPOSIT_YEAR)));
        assertTrue(isSortedDescendingOrderBasedOnDepositDate(defaultList));
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("search deposit for specific status in DONE OR ACTIVATED")
    public void searchDepositInSpecificStatuses_GivenListOfTheDeposit_thenReturnResults(
            List<Deposit> depositList, List<Status> statuses, AccountId accountId) {

        // Arrange
        expect(depositProvider.of(accountId)).andReturn(depositList);

        // Act
        replay(depositProvider);
        List<Deposit> listOfDeposits = consultListOfDeposit.searchDepositInSpecificStatuses(statuses, accountId);

        // Assert
        assertFalse(listOfDeposits.isEmpty());
        assertEquals(1, listOfDeposits.size());
        assertTrue(isSortedDescendingOrderBasedOnDepositDate(listOfDeposits));
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("search deposit for specific that contains a specific identifier")
    public void searchDepositContainingSpecificIdentifier_GivenListOfDeposit_thenReturnResults(List<Deposit> depositList, AccountId accountId) {

        // Arrange
        expect(depositProvider.of(accountId)).andReturn(depositList).anyTimes();

        // Act
        replay(depositProvider);
        List<Deposit> listOfDeposit = consultListOfDeposit.searchDepositIdentifierContainingSpecificCharacter(
                DepositFilter.builder().searchByIdentifier("DEP-2024").build(), accountId);
        List<Deposit> anotherList = consultListOfDeposit.searchDepositIdentifierContainingSpecificCharacter(
                DepositFilter.builder().searchByIdentifier("DEP").build(), accountId);

        // Assert
        assertEquals(1, listOfDeposit.size());
        assertEquals(2, anotherList.size());
        assertTrue(isSortedDescendingOrderBasedOnDepositDate(anotherList));
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("search deposit for specific status between dates")
    public void searchDepositWithSpecificStatusBetweenDates_GivenDepositStatusAndStartAndEndDate_thenReturnResult(
            List<Deposit> depositList, AccountId accountId) {

        // Arrange
        expect(depositProvider.of(accountId)).andReturn(depositList).anyTimes();

        // Act
        replay(depositProvider);
        List<Deposit> searchActivatedDepositsBetween = consultListOfDeposit.searchDepositWithSpecificStatusBetweenDates(
                DepositFilter.builder()
                        .searchByStatus(VALIDATED)
                        .searchByStartDate(new Date(1, Month.JANUARY, 2024))
                        .searchByEndDate(new Date(16, Month.MAY, 2024)).build(), accountId);

        List<Deposit> searchAnotherActivatedDepositsBetween = consultListOfDeposit.searchDepositWithSpecificStatusBetweenDates(
                DepositFilter.builder()
                        .searchByStatus(ACTIVATED)
                        .searchByStartDate(new Date(1, Month.JANUARY, 2023))
                        .searchByEndDate(new Date(16, Month.MAY, 2023)).build(), accountId);

        assertFalse(searchActivatedDepositsBetween.isEmpty());
        assertTrue(searchAnotherActivatedDepositsBetween.isEmpty());

    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("default list of deposit sorted in reversed order base on the deposit date")
    public void defaultList_whenDepositHasBeenDone_thenReturnListOfDepositSortedInReverseOrderBaseOnDepositDate(
            List<Deposit> depositList, AccountId accountId) {

        // Arrange
        expect(depositProvider.of(accountId)).andReturn(depositList);

        // Act
        replay(depositProvider);
        List<Deposit> listOfDeposits = consultListOfDeposit.withDefaultList(accountId);
        Deposit deposit = listOfDeposits.get(0);

        // Assert
        assertTrue(listOfDeposits.stream().allMatch(x -> Objects.equals(x.getDate().year(), CURRENT_DEPOSIT_YEAR)));
        assertTrue(isSortedDescendingOrderBasedOnDepositDate(listOfDeposits));
        assertEquals(CURRENT_DEPOSIT_YEAR, deposit.getDate().year());
        assertEquals(depositIdentifier1.value(), deposit.getIdentifier().value());
        assertEquals("deposit1", deposit.getName().text().value());
        assertEquals(money.value(), deposit.getMoney().value());
        assertEquals(ACTIVATED, deposit.getStatus());
        assertEquals(Month.APRIL, deposit.getExpirationDate().date().month());
        assertEquals(15, deposit.getExpirationDate().date().day());
        assertEquals(CURRENT_DEPOSIT_YEAR, deposit.getExpirationDate().date().year());
        assertNull(deposit.getUserExpirationDate());
        assertNull(deposit.getReason());
    }

    private static boolean isSortedDescendingOrderBasedOnDepositDate(List<Deposit> list) {
        if (list.size() == 1) {
            return true;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).getDate().compareTo(list.get(i + 1).getDate()) < 0) {
                return true;
            }
        }
        return false;
    }
}
