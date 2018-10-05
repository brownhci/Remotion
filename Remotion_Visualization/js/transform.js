var selector = '.box' // Replace this with the selector for the element you want to make transformable


jQuery.getScript('//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js', function() {
jQuery.getScript('//cdnjs.cloudflare.com/ajax/libs/numeric/1.2.6/numeric.min.js', function() {
  
(function($) {
  var $, applyTransform, getTransform, makeTransformable;

  $ = jQuery;

 
    
  getTransform = function(from, to) {
    var A, H, b, h, i, k_i, lhs, rhs, _i, _j, _k, _ref;
    console.assert((from.length === (_ref = to.length) && _ref === 4));
    A = [];
    for (i = _i = 0; _i < 4; i = ++_i) {
      A.push([from[i].x, from[i].y, 1, 0, 0, 0, -from[i].x * to[i].x, -from[i].y * to[i].x]);
      A.push([0, 0, 0, from[i].x, from[i].y, 1, -from[i].x * to[i].y, -from[i].y * to[i].y]);
    }
    b = [];
    for (i = _j = 0; _j < 4; i = ++_j) {
      b.push(to[i].x);
      b.push(to[i].y);
    }
    h = numeric.solve(A, b);
    H = [[h[0], h[1], 0, h[2]], [h[3], h[4], 0, h[5]], [0, 0, 1, 0], [h[6], h[7], 0, 1]];
    for (i = _k = 0; _k < 4; i = ++_k) {
      lhs = numeric.dot(H, [from[i].x, from[i].y, 0, 1]);
      k_i = lhs[3];
      rhs = numeric.dot(k_i, [to[i].x, to[i].y, 0, 1]);
      console.assert(numeric.norm2(numeric.sub(lhs, rhs)) < 1e-9, "Not equal:", lhs, rhs);
    }
    return H;
  };

  applyTransform = function(element, originalPos, targetPos, callback) {
    console.log(targetPos);
    var H, from, i, j, p, to;
    from = (function() {
      var _i, _len, _results;
      _results = [];
      for (_i = 0, _len = originalPos.length; _i < _len; _i++) {
        p = originalPos[_i];
        _results.push({
          x: p[0] - originalPos[0][0],
          y: p[1] - originalPos[0][1]
        });
      }
      return _results;
    })();
    to = (function() {
      var _i, _len, _results;
      _results = [];
      for (_i = 0, _len = targetPos.length; _i < _len; _i++) {
        p = targetPos[_i];
        _results.push({
          x: p[0] - originalPos[0][0],
          y: p[1] - originalPos[0][1]
        });
      }
      return _results;
    })();
    H = getTransform(from, to);
    // transform add rotation
    $(element).css({
      'transform': "matrix3d(" + (((function() {
        var _i, _results;
        _results = [];
        for (i = _i = 0; _i < 4; i = ++_i) {
          _results.push((function() {
            var _j, _results1;
            _results1 = [];
            for (j = _j = 0; _j < 4; j = ++_j) {
              _results1.push(H[j][i].toFixed(20));
            }
            return _results1;
          })());
        }
        return _results;
      })()).join(',')) + ")",
      'transform-origin': '0 0'
    });
    return typeof callback === "function" ? callback(element, H) : void 0;
  };

  makeTransformable = function(selector, callback) {
    return $(selector).each(function(i, element) {
      var controlPoints, originalPos, p, position;
      var ct = 0;
      $(element).css('transform', '');
      controlPoints = (function() {
        var _i, _len, _ref, _results;
        _ref = ['left top', 'left bottom', 'right top', 'right bottom'];
        _results = [];
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          position = _ref[_i];
          _results.push($('<div id=cp_'+ct+'>').css({
            border: '10px solid red',
            borderRadius: '10px',
            cursor: 'move',
            position: 'absolute',
            zIndex: 100000
          }).appendTo('body').position({
            at: position,
            of: element,
            collision: 'none'
          }));
          ct++;
        }
        return _results;
      })();
      originalPos = (function() {
        var _i, _len, _results;
        _results = [];
        for (_i = 0, _len = controlPoints.length; _i < _len; _i++) {
          p = controlPoints[_i];
          _results.push([p.offset().left, p.offset().top]);
        }
        return _results;
      })();
      $(controlPoints).draggable({
        start: (function(_this) {
          return function() {
            return $(element).css('pointer-events', 'none');
          };
        })(this),
        drag: (function(_this) {
          return function() {
            return applyTransform(element, originalPos, (function() {
              var _i, _len, _results;
              _results = [];
              for (_i = 0, _len = controlPoints.length; _i < _len; _i++) {
                p = controlPoints[_i];
                _results.push([p.offset().left, p.offset().top]);
              }
              return _results;
            })(), callback);
          };
        })(this),
        stop: (function(_this) {
          return function() {
            applyTransform(element, originalPos, (function() {
              var _i, _len, _results;
              _results = [];
              for (_i = 0, _len = controlPoints.length; _i < _len; _i++) {
                p = controlPoints[_i];
                _results.push([p.offset().left, p.offset().top]);
              }
              return _results;
            })(), callback);
            return $(element).css('pointer-events', 'auto');
          };
        })(this)
      });
      return element;
    });
  };

  window.makeTransformable = makeTransformable

}).call(this);

makeTransformable(selector);

});
});