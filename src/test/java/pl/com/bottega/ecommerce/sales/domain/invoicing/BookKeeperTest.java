package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductDataBuilder;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookKeeperTest {

    private static final String DUMMY_CLIENT_NAME = "Nowak";
    private static final Id DUMMY_ID = Id.generate();
    private static final ClientData DUMMY_CLIENT_DATA = new ClientData(DUMMY_ID, DUMMY_CLIENT_NAME);
    private static final String DUMMY_PRODUCT_DATA_NAME = "product name";
    private static final ProductData DUMMY_PRODUCT_DATA = new ProductDataBuilder()
            .withProductId(Id.generate())
            .withName(DUMMY_PRODUCT_DATA_NAME)
            .withSnapshotDate(new Date())
            .withType(ProductType.STANDARD)
            .withPrice(Money.ZERO)
            .build();
    private static final RequestItem DUMMY_REQUEST_ITEM = new RequestItem(DUMMY_PRODUCT_DATA, 2, Money.ZERO);
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
    void givenOneItemWhenInvoiceIssuanceThenReturnsOneItemInvoice() {

        invoiceRequest.add(DUMMY_REQUEST_ITEM);
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(DUMMY_TAX);

        Invoice dummyInvoice = new Invoice(DUMMY_ID, DUMMY_CLIENT_DATA);
        when(invoiceFactory.create(DUMMY_CLIENT_DATA)).thenReturn(dummyInvoice);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        int expectedNumberOfItems = 1;
        assertEquals(expectedNumberOfItems, invoice.getItems().size());
    }

}
