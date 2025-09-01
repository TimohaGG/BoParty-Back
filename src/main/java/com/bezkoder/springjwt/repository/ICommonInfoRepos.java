package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Order.CommonOrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICommonInfoRepos extends JpaRepository<CommonOrderInfo,Long> {
}
