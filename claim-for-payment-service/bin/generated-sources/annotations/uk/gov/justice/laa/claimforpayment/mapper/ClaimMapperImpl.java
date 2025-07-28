package uk.gov.justice.laa.claimforpayment.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.claimforpayment.entity.ClaimEntity;
import uk.gov.justice.laa.claimforpayment.model.Claim;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-28T13:08:14+0100",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.42.50.v20250628-1110, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class ClaimMapperImpl implements ClaimMapper {

    @Override
    public Claim toClaim(ClaimEntity claimEntity) {
        if ( claimEntity == null ) {
            return null;
        }

        Claim.ClaimBuilder claim = Claim.builder();

        claim.category( claimEntity.getCategory() );
        claim.claimed( claimEntity.getClaimed() );
        claim.client( claimEntity.getClient() );
        claim.concluded( claimEntity.getConcluded() );
        claim.feeType( claimEntity.getFeeType() );
        claim.id( claimEntity.getId() );
        claim.ufn( claimEntity.getUfn() );

        return claim.build();
    }

    @Override
    public ClaimEntity toClaimEntity(Claim claim) {
        if ( claim == null ) {
            return null;
        }

        ClaimEntity.ClaimEntityBuilder claimEntity = ClaimEntity.builder();

        claimEntity.category( claim.getCategory() );
        claimEntity.claimed( claim.getClaimed() );
        claimEntity.client( claim.getClient() );
        claimEntity.concluded( claim.getConcluded() );
        claimEntity.feeType( claim.getFeeType() );
        claimEntity.id( claim.getId() );
        claimEntity.ufn( claim.getUfn() );

        return claimEntity.build();
    }
}
