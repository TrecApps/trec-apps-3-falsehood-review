package com.trecapps.falsehoods.falsehoodReview.repos;

import com.trecapps.base.FalsehoodModel.models.Falsehood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface FalsehoodRepo extends JpaRepository<Falsehood, BigInteger> {
	

}
