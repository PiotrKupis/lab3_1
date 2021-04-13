package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductDataBuilder;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookKeeperTest {

    private static final String DUMMY_CLIENT_NAME = "Nowak";
    private static final Id DUMMY_ID = Id.generate();
    private static final ClientData DUMMY_CLIENT_DATA = new ClientData(DUMMY_ID, DUMMY_CLIENT_NAME);
    private static final String DUMMY_PRODUCT_DATA_NAME = "product name";
    private static final Tax DUMMY_TAX = new Tax(Money.ZERO, "dummy tax");

    @Mock
    private InvoiceFactory invoiceFactory;
    @Mock
    private TaxPolicy taxPolicy;
    private BookKeeper bookKeeper;
    private InvoiceRequest invoiceRequest;

    @BeforeEach
    void setUp() {
        bookKeeper = new BookKeeper(invoiceFactory);
        invoiceRequest = new InvoiceRequest(DUMMY_CLIENT_DATA);
    }

    @Test
    void givenRequestOfInvoiceWithOneElementShouldReturnInvoiceWithOneElement() {

        ProductData productData = new ProductDataBuilder()
                .withProductId(Id.generate())
                .withName(DUMMY_PRODUCT_DATA_NAME)
                .withSnapshotDate(new Date())
                .withType(ProductType.STANDARD)
                .withPrice(Money.ZERO)
                .build();
        RequestItem requestItem = new RequestItem(productData, 2, Money.ZERO);
        invoiceRequest.add(requestItem);

        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(DUMMY_TAX);
        Invoice dummyInvoice = new Invoice(DUMMY_ID, DUMMY_CLIENT_DATA);
        when(invoiceFactory.create(DUMMY_CLIENT_DATA)).thenReturn(dummyInvoice);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        int expectedNumberOfItems = 1;
        assertEquals(expectedNumberOfItems, invoice.getItems().size());
    }

    @Test
    void givenRequestOfInvoiceWithTwoElementsShouldInvokeCalculateTaxMethodTwice() {

        ProductData firstProductData = new ProductDataBuilder()
                .withProductId(Id.generate())
                .withName(DUMMY_PRODUCT_DATA_NAME)
                .withSnapshotDate(new Date())
                .withType(ProductType.STANDARD)
                .withPrice(Money.ZERO)
                .build();
        Money firstMoney = new Money(1, Money.DEFAULT_CURRENCY);
        RequestItem firstRequestItem = new RequestItem(firstProductData, 2, firstMoney);

        ProductData secondProductData = new ProductDataBuilder()
                .withProductId(Id.generate())
                .withName(DUMMY_PRODUCT_DATA_NAME)
                .withSnapshotDate(new Date())
                .withType(ProductType.STANDARD)
                .withPrice(Money.ZERO)
                .build();
        Money secondMoney = new Money(2, Money.DEFAULT_CURRENCY);
        RequestItem secondRequestItem = new RequestItem(secondProductData, 2, secondMoney);

        invoiceRequest.add(firstRequestItem);
        invoiceRequest.add(secondRequestItem);


        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(DUMMY_TAX);
        Invoice dummyInvoice = new Invoice(DUMMY_ID, DUMMY_CLIENT_DATA);
        when(invoiceFactory.create(DUMMY_CLIENT_DATA)).thenReturn(dummyInvoice);

        ArgumentCaptor<ProductType> productTypeCaptor = ArgumentCaptor.forClass(ProductType.class);
        ArgumentCaptor<Money> moneyCaptor = ArgumentCaptor.forClass(Money.class);

        bookKeeper.issuance(invoiceRequest, taxPolicy);

        int expectedNumberOfInvocation = 2;
        verify(taxPolicy, times(expectedNumberOfInvocation)).calculateTax(productTypeCaptor.capture(), moneyCaptor.capture());

        List<ProductType> capturedProductTypes = productTypeCaptor.getAllValues();
        List<Money> capturedMoney = moneyCaptor.getAllValues();

        assertEquals(firstProductData.getType(), capturedProductTypes.get(0));
        assertEquals(firstMoney, capturedMoney.get(0));

        assertEquals(secondProductData.getType(), capturedProductTypes.get(1));
        assertEquals(secondMoney, capturedMoney.get(1));
    }

    @Test
    void givenRequestOfInvoiceWithZeroElementShouldReturnInvoiceWithZeroElement() {

        Invoice dummyInvoice = new Invoice(DUMMY_ID, DUMMY_CLIENT_DATA);
        when(invoiceFactory.create(DUMMY_CLIENT_DATA)).thenReturn(dummyInvoice);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        int expectedNumberOfItems = 0;
        int result = invoice.getItems().size();
        assertEquals(expectedNumberOfItems, result);
    }

    @Test
    void givenRequestOfInvoiceWithZeroElementsShouldNotInvokeCalculateTaxMethod() {

        Invoice dummyInvoice = new Invoice(DUMMY_ID, DUMMY_CLIENT_DATA);
        when(invoiceFactory.create(DUMMY_CLIENT_DATA)).thenReturn(dummyInvoice);

        bookKeeper.issuance(invoiceRequest, taxPolicy);

        int expectedNumberOfInvocation = 0;
        verify(taxPolicy, times(expectedNumberOfInvocation)).calculateTax(any(ProductType.class), any(Money.class));
    }

    @Test
    public void invoiceFactoryShouldInvokeCreateMethodOneTime() {

        Invoice dummyInvoice = new Invoice(DUMMY_ID, DUMMY_CLIENT_DATA);
        when(invoiceFactory.create(DUMMY_CLIENT_DATA)).thenReturn(dummyInvoice);

        bookKeeper.issuance(invoiceRequest, taxPolicy);

        int expectedNumberOfInvocation = 1;
        verify(invoiceFactory, times(expectedNumberOfInvocation)).create(any(ClientData.class));
    }

}
