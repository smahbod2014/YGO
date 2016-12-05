-- Dian Keto the Cure Master

function c84257640.initialize(card)
    local effect = Duel.createEffect()
    effect:setActivationCriteria(Criteria.OnActivation)
    effect:setType(Type.LifepointRecovery)
    effect:setOperation(c84257640.operation)
    Duel.registerEffect(effect, card)
end

function c84257640.operation(activator)
    Duel.increaseLifepoints(activator, 1000)
end