-- Milus Radiant

function c7489323.initialize(card)
    local effect = Duel.createEffect()
    effect:setType(Type.ModifyAtk)
    effect:setFlags(Flag.Continuous)
    effect:setValue(500)
    effect:setFilter(c7489323.boostFilter)
    Duel.registerEffect(effect, card)

    effect = effect:copy()
    effect:setFilter(c7489323.reductionFilter)
    effect:setValue(-400)
    Duel.registerEffect(effect, card)
end

function c7489323.boostFilter(card)
    return card:getZone() == Zone.Monster and card:getAttribute() == Attribute.Earth
end

function c7489323.reductionFilter(card)
    return card:getZone() == Zone.Monster and card:getAttribute() == Attribute.Wind
end
