package com.shashirajraja.onlinebookstore.service;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shashirajraja.onlinebookstore.dao.BookUserRepository;
import com.shashirajraja.onlinebookstore.dao.CustomerRepository;
import com.shashirajraja.onlinebookstore.dao.PurchaseDetailRepository;
import com.shashirajraja.onlinebookstore.dao.PurchaseHistoryRepository;
import com.shashirajraja.onlinebookstore.entity.Book;
import com.shashirajraja.onlinebookstore.entity.BookUser;
import com.shashirajraja.onlinebookstore.entity.BookUserId;
import com.shashirajraja.onlinebookstore.entity.Customer;
import com.shashirajraja.onlinebookstore.entity.PurchaseDetail;
import com.shashirajraja.onlinebookstore.entity.PurchaseHistory;
import com.shashirajraja.onlinebookstore.entity.ShoppingCart;
import com.shashirajraja.onlinebookstore.utility.IDUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentServiceImpl implements PaymentService {

	private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

	@Autowired
	private CustomerRepository customerRepos;

	@Autowired
	private PurchaseHistoryRepository purchaseHistoryRepos;

	@Autowired
	private PurchaseDetailRepository purchaseDetailRepos;

	@Autowired
	private BookUserRepository bookUserRepos;

	@Override
	@Transactional
	public String createTransaction(Customer customer) {
		try {
			Set<ShoppingCart> items = customer.getShoppingCart();

			if (items.isEmpty()) {
				throw new IllegalArgumentException("Shopping cart is empty.");
			}

			PurchaseHistory purchaseHistory = createPurchaseHistory(customer, items);
			customer.addPurchaseHistories(purchaseHistory);
			customer.getShoppingCart().clear();

			customerRepos.save(customer);

			linkBooksToCustomer(customer, items);

			return purchaseHistory.getId();
		} catch (Exception e) {
			logger.error("Error during transaction creation", e);
			throw new RuntimeException("Transaction failed: " + e.getMessage());
		}
	}

	private PurchaseHistory createPurchaseHistory(Customer customer, Set<ShoppingCart> items) {
		PurchaseHistory purchaseHistory = new PurchaseHistory(IDUtil.generatePurchaseHistoryId(), new Date());
		purchaseHistory.setCustomer(customer);

		Set<PurchaseDetail> purchaseDetails = new HashSet<>();
		Set<Book> books = new HashSet<>();

		for (ShoppingCart item : items) {
			validateStock(item);
			Book book = updateStock(item);
			books.add(book);

			PurchaseDetail purchaseDetail = new PurchaseDetail(purchaseHistory, book, book.getPrice(),
					item.getQuantity());
			purchaseDetails.add(purchaseDetail);
		}

		purchaseHistory.setPurchaseDetails(purchaseDetails);
		return purchaseHistory;
	}

	private void validateStock(ShoppingCart item) {
		if (item.getBook().getQuantity() < item.getQuantity()) {
			throw new IllegalArgumentException(
					"Book named: " + item.getBook().getName() + " is out of stock!");
		}
	}

	private Book updateStock(ShoppingCart item) {
		Book book = item.getBook();
		book.setQuantity(book.getQuantity() - item.getQuantity());
		return book;
	}

	private void linkBooksToCustomer(Customer customer, Set<ShoppingCart> items) {
		for (ShoppingCart item : items) {
			Book book = item.getBook();
			BookUserId bookUserId = new BookUserId(book, customer);

			if (!bookUserRepos.findById(bookUserId).isPresent()) {
				int result = bookUserRepos.addBookToUser(book.getId(), customer.getUsername());
				if (result <= 0) {
					throw new RuntimeException("Failed to link book and user: " + book.getName());
				}
			}
		}
	}

	@Override
	@Transactional
	public Set<PurchaseHistory> getPurchaseHistories(Customer customer) {
		Set<PurchaseHistory> histories = new HashSet<>(purchaseHistoryRepos.findAllByCustomer(customer));
		customer.setPurchaseHistories(histories);
		return histories;
	}

	@Override
	@Transactional
	public Set<PurchaseDetail> getPurchaseDetails(PurchaseHistory purchaseHistory) {
		return new HashSet<>(purchaseDetailRepos.findAllByHistory(purchaseHistory));
	}

	@Override
	@Transactional
	public PurchaseHistory getPurchaseHistory(Customer customer, String transId) {
		Optional<PurchaseHistory> historyOpt = purchaseHistoryRepos.findById(transId);
		if (historyOpt.isPresent()) {
			PurchaseHistory purchaseHistory = historyOpt.get();
			if (purchaseHistory.getCustomer().getUsername().equals(customer.getUsername())) {
				return purchaseHistory;
			}
		}
		return null;
	}
}
