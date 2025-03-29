package mate.academy.spring.online.bookstore.repository.cartitem;

import java.util.Optional;
import mate.academy.spring.online.bookstore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("FROM CartItem  WHERE shoppingCart.id = :shoppingCartId AND book.id = :bookId")
    CartItem findByShoppingCartIdAndBookId(Long shoppingCartId, Long bookId);

    Optional<CartItem> findByIdAndShoppingCartId(Long itemId, Long cartId);
}
