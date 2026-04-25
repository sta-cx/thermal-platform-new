package org.sdkj.thermal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.sdkj.thermal.domain.PrPet;
import org.sdkj.thermal.mapper.PrPetMapper;
import org.sdkj.thermal.service.IPrPetService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrPetServiceImpl extends ServiceImpl<PrPetMapper, PrPet>
        implements IPrPetService {

    private final PrPetMapper baseMapper;
}
