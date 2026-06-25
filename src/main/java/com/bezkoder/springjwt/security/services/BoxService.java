package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.models.Box.Box;
import com.bezkoder.springjwt.models.Box.BoxAdditionalService;
import com.bezkoder.springjwt.models.Box.BoxPositionAmount;
import com.bezkoder.springjwt.models.Position.Position;
import com.bezkoder.springjwt.payload.request.Boxes.BoxAdditionalServiceRequest;
import com.bezkoder.springjwt.payload.request.Boxes.BoxPositionRequest;
import com.bezkoder.springjwt.payload.request.Boxes.BoxRequest;
import com.bezkoder.springjwt.payload.response.Boxes.BoxResponse;
import com.bezkoder.springjwt.repository.BoxRepos;
import com.bezkoder.springjwt.repository.PositionsRepos;
import com.bezkoder.springjwt.security.Exceptions.NoContentException;
import com.bezkoder.springjwt.security.Exceptions.OrderCreateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BoxService {
    private final BoxRepos boxRepos;
    private final PositionsRepos positionsRepos;

    public BoxService(BoxRepos boxRepos, PositionsRepos positionsRepos) {
        this.boxRepos = boxRepos;
        this.positionsRepos = positionsRepos;
    }

    public List<BoxResponse> getAll() {
        return this.boxRepos.findAll()
                .stream()
                .map(Box::toResponse)
                .toList();
    }

    @Transactional
    public BoxResponse create(BoxRequest req) {
        Box box = new Box();
        apply(box, req);

        try {
            return this.boxRepos.save(box).toResponse();
        } catch (Exception e) {
            throw new OrderCreateException("Can't create box: " + e.getMessage());
        }
    }

    @Transactional
    public BoxResponse edit(BoxRequest req) {
        if (req.getId() == null) {
            throw new NoContentException("Box id is required");
        }

        Box box = this.boxRepos.findById(req.getId())
                .orElseThrow(() -> new NoContentException("Box not found"));

        apply(box, req);

        try {
            return this.boxRepos.save(box).toResponse();
        } catch (Exception e) {
            throw new OrderCreateException("Can't edit box: " + e.getMessage());
        }
    }

    @Transactional
    public Long delete(Long id) {
        Box box = this.boxRepos.findById(id)
                .orElseThrow(() -> new NoContentException("Box not found"));

        this.boxRepos.delete(box);
        return id;
    }

    private void apply(Box box, BoxRequest req) {
        if (req.getName() == null || req.getName().trim().isEmpty()) {
            throw new NoContentException("Box name is required");
        }

        if (req.getPositions() == null || req.getPositions().isEmpty()) {
            throw new NoContentException("Box should contain at least one position");
        }

        box.setName(req.getName().trim());
        box.setDescription(req.getDescription());
        box.getPositions().clear();
        box.getAdditionalServices().clear();

        double totalPrice = 0;
        for (BoxPositionRequest item : req.getPositions()) {
            Position position = this.positionsRepos.findById(item.getPositionId())
                    .orElseThrow(() -> new NoContentException("Position not found: " + item.getPositionId()));

            int amount = item.getAmount() == null ? position.getMinimumAmount() : item.getAmount();
            amount = Math.max(amount, position.getMinimumAmount());

            BoxPositionAmount boxPosition = BoxPositionAmount.builder()
                    .box(box)
                    .position(position)
                    .amount(amount)
                    .build();

            box.addPosition(boxPosition);
            totalPrice += position.getPrice() * amount;
        }

        List<BoxAdditionalServiceRequest> additionalServices = req.getAdditionalServices() == null
                ? List.of()
                : req.getAdditionalServices();

        for (BoxAdditionalServiceRequest item : additionalServices) {
            if (item.getText() == null || item.getText().trim().isEmpty()) {
                throw new NoContentException("Additional service text is required");
            }

            BoxAdditionalService additionalService = BoxAdditionalService.fromRequest(item, box);
            box.addAdditionalService(additionalService);
            totalPrice += item.getPrice();
        }

        box.setTotalPrice(totalPrice);
    }
}
