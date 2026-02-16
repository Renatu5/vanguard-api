package com.renato.vanguard_api.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.renato.vanguard_api.model.PaginationResponse;
import com.renato.vanguard_api.model.card.Card;
import com.renato.vanguard_api.model.card.Unit;
import com.renato.vanguard_api.model.card.trigger.TriggerOrder;
import com.renato.vanguard_api.model.card.trigger.TriggerUnit;
import com.renato.vanguard_api.repository.CardRepository;

@Service
public class CardService {
    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public PaginationResponse<Card> listAllCards(Pageable pageable) {
        Page<Card> pageResult = cardRepository.findAll(pageable);
        String baseUrl = "/api/cards";
        String next = pageResult.hasNext()
                ? String.format("%s?page=%d&size=%d&ascending=%b", baseUrl, pageable.getPageNumber() + 1,
                        pageable.getPageSize(), pageable.getSort())
                : null;
        String previous = pageResult.hasPrevious()
                ? String.format("%s?page=%d&size=%d&ascending=%b", baseUrl, pageable.getPageNumber() - 1,
                        pageable.getPageSize(), pageable.getSort())
                : null;
        String url = String.format("%s?page=%d&size=%d&ascending=%b", baseUrl, pageable.getPageNumber(),
                pageable.getPageSize(), pageable.getSort());
        return new PaginationResponse<>(previous, next, url, pageResult.getContent());
    }

    public Optional<Card> findById(Long id) {
        return cardRepository.findById(id);
    }

    public List<Card> findCardsByProduct(Long productId) {
        return cardRepository.findByProductId(productId);
    }

    public Card saveCard(Card card) {
        Card newCard;
        if (card instanceof TriggerUnit) {
            if (card instanceof TriggerOrder) {
                newCard = new TriggerOrder((TriggerOrder) card);
            } else {
                newCard = new TriggerUnit((TriggerUnit) card);
            }
            if (card instanceof Unit)
                newCard = new Unit((Unit) card);
        } else {
            newCard = new Card(card);
        }
        return cardRepository.save(newCard);
    }

    public void deleteCardById(Long id) {
        cardRepository.deleteById(id);
    }

    public Card updateCard(Long id, Card updatedCard) {
        if (!cardRepository.existsById(id)) {
            throw new IllegalStateException("Card with the given ID not found: " + id);
        } else {
            Optional<Card> existingCardOpt = cardRepository.findById(id);
            Card existingCard = existingCardOpt.get();
            existingCard.setName(updatedCard.getName());
            existingCard.setGrade(updatedCard.getGrade());
            existingCard.setSpecialIcon(updatedCard.getSpecialIcon());
            existingCard.setFlavorText(updatedCard.getFlavorText());
            existingCard.setNation(updatedCard.getNation());
            existingCard.setRace(updatedCard.getRace());
            existingCard.setClan(updatedCard.getClan());
            existingCard.setType(updatedCard.getType());
            existingCard.setFormat(updatedCard.getFormat());
            existingCard.setCardEffect(updatedCard.getCardEffect());
            existingCard.setSource(updatedCard.getSource());
            existingCard.setImageSource(updatedCard.getImageSource());
            existingCard.setRarities(updatedCard.getRarities());
            existingCard.setArtist(updatedCard.getArtist());

            if (existingCard instanceof Unit && updatedCard instanceof Unit) {
                Unit existingUnit = (Unit) existingCard;
                Unit updatedUnit = (Unit) updatedCard;
                existingUnit.setAbility(updatedUnit.getAbility());
                existingUnit.setPower(updatedUnit.getPower());
                existingUnit.setShield(updatedUnit.getShield());
                existingUnit.setCritical(updatedUnit.getCritical());
            }
            if (existingCard instanceof TriggerUnit && updatedCard instanceof TriggerUnit) {
                TriggerUnit existingTrigger = (TriggerUnit) existingCard;
                TriggerUnit updatedTrigger = (TriggerUnit) updatedCard;
                existingTrigger.setType(updatedTrigger.getType());
                existingTrigger.setTriggerEffect(updatedTrigger.getTriggerEffect());
            }
            if (existingCard instanceof TriggerOrder && updatedCard instanceof TriggerOrder) {
                TriggerOrder existingOrder = (TriggerOrder) existingCard;
                TriggerOrder updatedOrder = (TriggerOrder) updatedCard;
                existingOrder.setType(updatedOrder.getType());
                existingOrder.setTriggerEffect(updatedOrder.getTriggerEffect());
            }

            return cardRepository.save(existingCard);
        }
    }
}
