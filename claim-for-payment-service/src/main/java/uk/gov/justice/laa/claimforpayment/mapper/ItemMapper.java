package uk.gov.justice.laa.claimforpayment.mapper;

import org.mapstruct.Mapper;
import uk.gov.justice.laa.claimforpayment.entity.ItemEntity;
import uk.gov.justice.laa.claimforpayment.model.Item;

/**
 * The mapper between Item and ItemEntity.
 */
@Mapper(componentModel = "spring")
public interface ItemMapper {

  /**
   * Maps the given item entity to an item.
   *
   * @param itemEntity the item entity
   * @return the item
   */
  Item toItem(ItemEntity itemEntity);

  /**
   * Maps the given item to an item entity.
   *
   * @param item the item
   * @return the item entity
   */
  ItemEntity toItemEntity(Item item);
}
