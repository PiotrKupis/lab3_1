package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;

import static org.mockito.Mockito.when;

@ExtendWith((MockitoExtension.class))
class BookKeeperTest {

    private static final String DUMMY_CLIENT_NAME = "Nowak";
    private static final ClientData DUMMY_CLIENT = new ClientData(Id.generate(), DUMMY_CLIENT_NAME);

    @Mock
    private InvoiceFactory invoiceFactory;
    @Mock
    private TaxPolicy taxPolicy;
    private BookKeeper bookKeeper;
    private InvoiceRequest invoiceRequest;

    @BeforeEach
    void setUp() {
        bookKeeper = new BookKeeper(invoiceFactory);
        invoiceRequest = new InvoiceRequest(DUMMY_CLIENT);
    }

    @Test
    void givenOneItemWhenInvoiceIssuanceThenReturnsOneItemInvoice() {
    }

}
