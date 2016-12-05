-- Yami

function c59197169.initialize(card)
    local effect = Duel.createEffect()
    effect:setActivationCriteria(Criteria.OnActivation)
    effect:setType(Type.ModifyAtk)
    effect:setValue(200)
    effect:setFlags(Flag.Continuous)
    effect:setFilter(c59197169.boostFilter)
    Duel.registerEffect(effect, card)

    effect = effect:copy()
    effect:setType(Type.ModifyDef)
    Duel.registerEffect(effect, card)

    effect = effect:copy()
    effect:setType(Type.ModifyAtk)
    effect:setFilter(c59197169.reductionFilter)
    effect:setValue(-200)
    Duel.registerEffect(effect, card)

    effect = effect:copy()
    effect:setType(Type.ModifyDef)
    Duel.registerEffect(effect, card)
end

function c59197169.boostFilter(card)
    local race = card:getRace()
    return card:getZone() == Zone.Monster and (race == Race.Fiend or race == Race.Spellcaster)
end

function c59197169.reductionFilter(card)
    return card:getZone() == Zone.Monster and card:getRace() == Race.Fairy
end
