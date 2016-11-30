-- Yami

function c59197169.initialize(card)
    local effect = Duel.createEffect()
    effect:setActivationCriteria(Criteria.OnActivation)
    effect:setTypes(Type.ModifyAtk, Type.ModifyDef)
    effect:setValue(200)
    effect:setFlags(Flag.Continuous)
    effect:setFilter(c59197169.boostFilter)
    Duel.registerEffect(effect, card)
    effect = effect:copy()
    effect:setFilter(c59197169.reductionFilter)
    Duel.registerEffect(effect, card)
end

function c59197169.boostFilter(card)
    local race = card:getRace()
    return card:getZone() == Zone.Monster and (race == Race.Fiend or race == Race.Spellcaster or race == Race.Warrior)
end

function c59197169.reductionFilter(card)
    return card:getZone() == Zone.Monster and card:getRace() == Race.Fairy
end
